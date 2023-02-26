package com.majruszsdifficulty.undeadarmy;

import com.google.gson.JsonElement;
import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.undeadarmy.data.UndeadArmyInfo;
import com.majruszsdifficulty.undeadarmy.data.WaveDef;
import com.majruszsdifficulty.undeadarmy.data.WavesDef;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.BooleanConfig;
import com.mlib.config.DoubleConfig;
import com.mlib.config.IntegerConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnDeath;
import com.mlib.gamemodifiers.contexts.OnLoot;
import com.mlib.gamemodifiers.contexts.OnServerTick;
import com.mlib.items.ItemHelper;
import com.mlib.loot.LootHelper;
import com.mlib.math.Range;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@AutoInstance
public class Config extends GameModifier {
	static ResourceLocation EXTRA_LOOT_ID = Registries.getLocation( "undead_army/extra_mob_loot" );
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

		new OnLoot.Context( this::giveExtraLoot )
			.addCondition( new Condition.IsServer<>() )
			.addCondition( OnLoot.HAS_DAMAGE_SOURCE )
			.addCondition( data->!data.context.getQueriedLootTableId().equals( EXTRA_LOOT_ID ) )
			.addCondition( data->data.entity instanceof Mob mob && mob.getMobType() == MobType.UNDEAD )
			.addCondition( data->Registries.UNDEAD_ARMY_MANAGER.isPartOfUndeadArmy( data.entity ) )
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
		return this.getWaves().size();
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
		List< WaveDef > waves = this.getWaves();

		return waves.get( Mth.clamp( waveIdx - 1, 0, waves.size() - 1 ) );
	}

	public List< WaveDef > getWaves() {
		return this.wavesDef.get()
			.stream()
			.filter( waveDef->GameStage.atLeast( waveDef.gameStage ) )
			.toList();
	}

	private void updateKilledUndead( OnDeath.Data data ) {
		ServerPlayer player = ( ServerPlayer )data.attacker;
		CompoundTag tag = player.getPersistentData();
		UndeadArmyInfo info = new UndeadArmyInfo();
		info.read( tag );

		++info.killedUndead;
		if( info.killedUndead >= this.getRequiredKills() && Registries.UNDEAD_ARMY_MANAGER.tryToSpawn( player ) ) {
			info.killedUndead = 0;
		} else if( info.killedUndead == this.getRequiredKills() - 3 ) {
			player.sendSystemMessage( Component.translatable( "majruszsdifficulty.undead_army.warning" ).withStyle( ChatFormatting.DARK_PURPLE ) );
		}

		info.write( tag );
	}

	private void giveExtraLoot( OnLoot.Data data ) {
		List< ItemStack > extraLoot = LootHelper.getLootTable( EXTRA_LOOT_ID )
			.getRandomItems( this.toExtraLootContext( data ) );

		data.generatedLoot.addAll( extraLoot );
	}

	private LootContext toExtraLootContext( OnLoot.Data data ) {
		return new LootContext.Builder( data.level )
			.withParameter( LootContextParams.ORIGIN, data.entity.position() )
			.withParameter( LootContextParams.THIS_ENTITY, data.entity )
			.withParameter( LootContextParams.DAMAGE_SOURCE, data.damageSource )
			.withOptionalParameter( LootContextParams.KILLER_ENTITY, data.killer )
			.create( LootContextParamSets.ENTITY );
	}
}
