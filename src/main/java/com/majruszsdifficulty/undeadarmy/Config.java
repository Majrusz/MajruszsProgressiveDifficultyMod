package com.majruszsdifficulty.undeadarmy;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.majruszsdifficulty.Registries;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.BooleanConfig;
import com.mlib.config.DoubleConfig;
import com.mlib.config.IntegerConfig;
import com.mlib.data.SerializableStructure;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnDeath;
import com.mlib.gamemodifiers.contexts.OnServerTick;
import com.mlib.math.Range;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoInstance
public class Config extends GameModifier {
	private final BooleanConfig availability = new BooleanConfig( true );
	private final DoubleConfig waveDuration = new DoubleConfig( 1200.0, new Range<>( 300.0, 3600.0 ) );
	private final DoubleConfig preparationDuration = new DoubleConfig( 10.0, new Range<>( 4.0, 30.0 ) );
	private final DoubleConfig highlightDelay = new DoubleConfig( 300.0, new Range<>( 30.0, 3600.0 ) );
	private final DoubleConfig extraSizePerPlayer = new DoubleConfig( 0.5, new Range<>( 0.0, 1.0 ) );
	private final IntegerConfig armyRadius = new IntegerConfig( 70, new Range<>( 35, 140 ) );
	private final IntegerConfig killRequirement = new IntegerConfig( 75, new Range<>( 0, 1000 ) );
	private WavesDef wavesDef = null;

	public Config() {
		super( Registries.Modifiers.UNDEAD_ARMY );

		var gson = Deserializers.createFunctionSerializer().registerTypeAdapter( WavesDef.class, new WavesDef.Serializer() ).create();
		var listener = new SimpleJsonResourceReloadListener( gson, "undead_army" ) {
			@Override
			protected void apply( Map< ResourceLocation, JsonElement > elements, ResourceManager manager, ProfilerFiller filler ) {
				Config.this.wavesDef = gson.fromJson( elements.get( Registries.getLocation( "waves" ) ), WavesDef.class );
			}
		};
		MinecraftForge.EVENT_BUS.addListener( ( AddReloadListenerEvent event )->event.addListener( listener ) );

		new OnServerTick.Context( data->Registries.UNDEAD_ARMY_MANAGER.tick() )
			.addCondition( data->data.event.phase == TickEvent.Phase.END )
			.insertTo( this );

		new OnDeath.Context( this::updateKilledUndead )
			.addCondition( data->this.getRequiredKills() > 0 )
			.addCondition( data->data.target.getMobType() == MobType.UNDEAD )
			.addCondition( data->!Registries.UNDEAD_ARMY_MANAGER.isPartOfUndeadArmy( data.target ) )
			.addCondition( data->data.attacker instanceof ServerPlayer )
			.insertTo( this );

		this.addConfig( this.availability.name( "is_enabled" ).comment( "Determines whether the Undead Army can spawn in any way." ) )
			.addConfig( this.waveDuration.name( "wave_duration" ).comment( "Duration that players have to defeat a single wave (in seconds)." ) )
			.addConfig( this.preparationDuration.name( "preparation_duration" ).comment( "Duration before the next wave arrives (in seconds)." ) )
			.addConfig( this.highlightDelay.name( "highlight_delay" ).comment( "Duration before all mobs will be highlighted (in seconds)." ) )
			.addConfig( this.extraSizePerPlayer.name( "extra_size_per_player" )
				.comment( "Extra size ratio per each additional player on multiplayer (0.25 means ~25% bigger army per player)." ) )
			.addConfig( this.armyRadius.name( "army_radius" ).comment( "Radius, which determines how big is the raid circle (in blocks)." ) )
			.addConfig( this.killRequirement.name( "kill_requirement" )
				.comment( "Required amount of killed undead to start the Undead Army. (set to 0 if you want to disable this)" ) );
	}

	public boolean isEnabled() {
		return this.availability.isEnabled();
	}

	public int getWaveDuration() {
		return this.waveDuration.asTicks();
	}

	public int getPreparationDuration() {
		return this.preparationDuration.asTicks();
	}

	public float getSizeMultiplier( int playerCount ) {
		return 1.0f + Math.max( playerCount - 1, 0 ) * this.extraSizePerPlayer.asFloat();
	}

	public int getWavesNum() {
		return this.wavesDef.get().size();
	}

	public int getArmyRadius() {
		return this.armyRadius.get();
	}

	public int getHighlightDelay() {
		return this.highlightDelay.asTicks();
	}

	public int getRequiredKills() {
		return this.killRequirement.get();
	}

	public int getSpawnRadius() {
		return this.getArmyRadius() - 15; // maybe one day add a config
	}

	public WaveDef getWave( int waveIdx ) {
		return this.wavesDef.get().get( Mth.clamp( waveIdx - 1, 0, this.getWavesNum() - 1 ) );
	}

	private void updateKilledUndead( OnDeath.Data data ) {
		ServerPlayer player = ( ServerPlayer )data.attacker;
		CompoundTag tag = player.getPersistentData();
		UndeadArmyInfo info = new UndeadArmyInfo();
		info.read( tag );

		++info.data.killedUndead;
		if( info.data.killedUndead >= this.getRequiredKills() && Registries.UNDEAD_ARMY_MANAGER.tryToSpawn( player ) ) {
			info.data.killedUndead = 0;
		} else if( info.data.killedUndead == this.getRequiredKills() - 3 ) {
			player.sendSystemMessage( Component.translatable( "majruszsdifficulty.undead_army.warning" ).withStyle( ChatFormatting.DARK_PURPLE ) );
		}

		info.write( tag );
	}

	static class WavesDef extends SerializableStructure {
		final List< WaveDef > waveDefs = new ArrayList<>();

		public WavesDef() {
			this.define( null, ()->this.waveDefs, this.waveDefs::addAll, WaveDef::new );
		}

		public List< WaveDef > get() {
			return this.waveDefs;
		}

		static class Serializer implements JsonDeserializer< WavesDef > {
			@Override
			public WavesDef deserialize( JsonElement element, Type type, JsonDeserializationContext context ) throws JsonParseException {
				WavesDef wavesDef = new WavesDef();
				wavesDef.read( element );

				return wavesDef;
			}
		}
	}

	static class WaveDef extends SerializableStructure {
		final List< MobDef > mobDefs = new ArrayList<>();
		MobDef boss;
		int experience = 0;

		public WaveDef() {
			this.define( "mobs", ()->this.mobDefs, this.mobDefs::addAll, MobDef::new );
			this.define( "boss", ()->this.boss, x->this.boss = x, MobDef::new );
			this.define( "exp", ()->this.experience, x->this.experience = x );
		}
	}

	static class MobDef extends SerializableStructure {
		EntityType< ? > type;
		int count = 1;
		ResourceLocation equipment = LootTable.EMPTY.getLootTableId();

		public MobDef() {
			this.define( "type", ()->this.type, x->this.type = x );
			this.define( "count", ()->this.count, x->this.count = x );
			this.define( "equipment", ()->this.equipment, x->this.equipment = x );
		}
	}

	static class UndeadArmyInfo extends SerializableStructure {
		Data data = new Data();

		public UndeadArmyInfo() {
			this.define( "UndeadArmy", ()->this.data, x->this.data = x, Data::new );
		}

		static class Data extends SerializableStructure {
			int killedUndead = 0;

			public Data() {
				this.define( "killed_undead", ()->this.killedUndead, x->this.killedUndead = x );
			}
		}
	}
}
