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
import com.mlib.math.Range;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoInstance
public class Config extends GameModifier {
	private final BooleanConfig availability = new BooleanConfig( true );
	private final DoubleConfig waveDuration = new DoubleConfig( 1200.0, new Range<>( 300.0, 3600.0 ) );
	private final DoubleConfig preparationDuration = new DoubleConfig( 10.0, new Range<>( 4.0, 30.0 ) );
	private final DoubleConfig extraSizePerPlayer = new DoubleConfig( 0.5, new Range<>( 0.0, 1.0 ) );
	private final IntegerConfig armyRadius = new IntegerConfig( 70, new Range<>( 35, 140 ) );
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

		this.addConfig( this.availability.name( "is_enabled" ).comment( "Determines whether the Undead Army can spawn in any way." ) )
			.addConfig( this.waveDuration.name( "wave_duration" ).comment( "Duration that players have to defeat a single wave (in seconds)." ) )
			.addConfig( this.preparationDuration.name( "preparation_duration" ).comment( "Duration before the next wave arrives (in seconds)." ) )
			.addConfig( this.extraSizePerPlayer.name( "extra_size_per_player" ).comment( "Extra size ratio per each additional player on multiplayer (0.25 means ~25% bigger army per player)." ) )
			.addConfig( this.armyRadius.name( "army_radius" ).comment( "Radius, which determines how big is the raid circle (in blocks)." ) );
	}

	public boolean isDisabled() {
		return this.availability.isDisabled();
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

	public int getSpawnRadius() {
		return this.getArmyRadius() - 15; // maybe one day add a config
	}

	public WaveDef getWave( int waveIdx ) {
		return this.wavesDef.get().get( Mth.clamp( waveIdx - 1, 0, this.getWavesNum() - 1 ) );
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

		public WaveDef() {
			this.define( "mobs", ()->this.mobDefs, this.mobDefs::addAll, MobDef::new );
			this.define( "boss", ()->this.boss, x->this.boss = x, MobDef::new );
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
}
