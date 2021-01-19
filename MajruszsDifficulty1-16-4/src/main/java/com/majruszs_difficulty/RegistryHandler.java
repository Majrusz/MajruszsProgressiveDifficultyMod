package com.majruszs_difficulty;

import com.google.common.collect.ImmutableMap;
import com.majruszs_difficulty.commands.ChangeGameStateCommand;
import com.majruszs_difficulty.commands.StopUndeadArmyCommand;
import com.majruszs_difficulty.entities.EliteSkeletonEntity;
import com.majruszs_difficulty.entities.GiantEntity;
import com.majruszs_difficulty.entities.PillagerWolfEntity;
import com.majruszs_difficulty.entities.SkyKeeperEntity;
import com.majruszs_difficulty.events.treasure_bag.TreasureBagManager;
import com.majruszs_difficulty.events.undead_army.ReloadUndeadArmyGoals;
import com.majruszs_difficulty.events.undead_army.UndeadArmyManager;
import com.majruszs_difficulty.structure_pieces.FlyingPhantomPiece;
import com.majruszs_difficulty.structures.FlyingPhantomStructure;
import com.mlib.items.SpawnEggFactory;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/** Main class registering most registers like entities, items and sounds. */
public class RegistryHandler {
	public static final DeferredRegister< EntityType< ? > > ENTITIES = DeferredRegister.create( ForgeRegistries.ENTITIES, MajruszsDifficulty.MOD_ID );
	public static final DeferredRegister< Item > ITEMS = DeferredRegister.create( ForgeRegistries.ITEMS, MajruszsDifficulty.MOD_ID );
	public static final DeferredRegister< SoundEvent > SOUNDS = DeferredRegister.create( ForgeRegistries.SOUND_EVENTS, MajruszsDifficulty.MOD_ID );
	public static final DeferredRegister< Effect > EFFECTS = DeferredRegister.create( ForgeRegistries.POTIONS, MajruszsDifficulty.MOD_ID );
	public static final DeferredRegister< Structure< ? > > STRUCTURES = DeferredRegister.create( ForgeRegistries.STRUCTURE_FEATURES, MajruszsDifficulty.MOD_ID );

	// Structures
	public static final RegistryObject< Structure< NoFeatureConfig > > FLYING_PHANTOM = STRUCTURES.register( "flying_phantom_structure", ()->FlyingPhantomStructure.INSTANCE );

	// Structure pieces
	public static IStructurePieceType FLYING_PHANTOM_PIECE = IStructurePieceType.register( FlyingPhantomPiece::new, MajruszsHelper.getResource( "flying_phantom" ).toString() );

	public static UndeadArmyManager UNDEAD_ARMY_MANAGER;
	public static GameDataSaver GAME_DATA_SAVER;

	public static void init() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get()
			.getModEventBus();

		registerEverything( modEventBus );
		modEventBus.addListener( RegistryHandler::setup );
		modEventBus.addListener( RegistryHandlerClient::setup );

		IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
		forgeEventBus.addListener( RegistryHandler::onLoadingWorld );
		forgeEventBus.addListener( RegistryHandler::onSavingWorld );
		forgeEventBus.addListener( RegistryHandler::onServerStart );
		forgeEventBus.addListener( RegistryHandler::registerCommands );
	}

	private static void registerEntities( final IEventBus modEventBus ) {
		ENTITIES.register( "giant", ()->GiantEntity.type );
		ENTITIES.register( "pillager_wolf", ()->PillagerWolfEntity.type );
		ENTITIES.register( "elite_skeleton", ()->EliteSkeletonEntity.type );
		ENTITIES.register( "sky_keeper", ()->SkyKeeperEntity.type );
		ENTITIES.register( modEventBus );
	}

	private static void registerSpawnEggs() {
		SpawnEggFactory.createRegistrySpawnEgg( ITEMS, "giant_spawn_egg", GiantEntity.type, 44975, 7969893 );
		SpawnEggFactory.createRegistrySpawnEgg( ITEMS, "pillager_wolf_spawn_egg", PillagerWolfEntity.type, 9804699, 5451574 );
		SpawnEggFactory.createRegistrySpawnEgg( ITEMS, "illusioner_spawn_egg", EntityType.ILLUSIONER, 0x135a97, 9804699 );
		SpawnEggFactory.createRegistrySpawnEgg( ITEMS, "elite_skeleton_spawn_egg", EliteSkeletonEntity.type, 12698049, 0xFE484D );
		SpawnEggFactory.createRegistrySpawnEgg( ITEMS, "sky_keeper_spawn_egg", SkyKeeperEntity.type, 0x7B45AD, 0xF0F0F0 );
	}

	private static void registerTreasureBags() {
		Instances.TreasureBags.UNDEAD_ARMY.register();
		Instances.TreasureBags.ELDER_GUARDIAN.register();
		Instances.TreasureBags.WITHER.register();
		Instances.TreasureBags.ENDER_DRAGON.register();
		Instances.TreasureBags.FISHING.register();
	}

	private static void registerItems( final IEventBus modEventBus ) {
		registerSpawnEggs();
		registerTreasureBags();
		ITEMS.register( "wither_sword", ()->Instances.Tools.WITHER_SWORD );
		ITEMS.register( "undead_battle_standard", ()->Instances.Miscellaneous.BATTLE_STANDARD_ITEM );
		ITEMS.register( "bandage", ()->Instances.Miscellaneous.BANDAGE );
		ITEMS.register( modEventBus );
	}

	private static void registerSounds( final IEventBus modEventBus ) {
		SOUNDS.register( "undead_army.approaching", ()->Instances.Sounds.UNDEAD_ARMY_APPROACHING );
		SOUNDS.register( "undead_army.wave_started", ()->Instances.Sounds.UNDEAD_ARMY_WAVE_STARTED );
		SOUNDS.register( modEventBus );
	}

	private static void registerEffects( final IEventBus modEventBus ) {
		EFFECTS.register( "bleeding", ()->Instances.Effects.BLEEDING );
		EFFECTS.register( modEventBus );
	}

	private static void registerStructures( final IEventBus modEventBus ) {
		STRUCTURES.register( modEventBus );

		Structure.NAME_STRUCTURE_BIMAP.put( "flying_phantom_structure", FlyingPhantomStructure.INSTANCE );
	}

	private static void registerEverything( final IEventBus modEventBus ) {
		registerEntities( modEventBus );
		registerItems( modEventBus );
		registerSounds( modEventBus );
		registerEffects( modEventBus );
		registerStructures( modEventBus );
	}

	private static void setup( final FMLCommonSetupEvent event ) {
		GlobalEntityTypeAttributes.put( GiantEntity.type, GiantEntity.getAttributeMap() );
		GlobalEntityTypeAttributes.put( PillagerWolfEntity.type, PillagerWolfEntity.getAttributeMap() );
		GlobalEntityTypeAttributes.put( EliteSkeletonEntity.type, EliteSkeletonEntity.getAttributeMap() );
		GlobalEntityTypeAttributes.put( SkyKeeperEntity.type, SkyKeeperEntity.getAttributeMap() );

		EntitySpawnPlacementRegistry.register( GiantEntity.type, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
			GiantEntity::canMonsterSpawn );
		EntitySpawnPlacementRegistry.register( PillagerWolfEntity.type, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
			PillagerWolfEntity::canAnimalSpawn );
		EntitySpawnPlacementRegistry.register( EliteSkeletonEntity.type, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
			EliteSkeletonEntity::canMonsterSpawn );
		EntitySpawnPlacementRegistry.register( SkyKeeperEntity.type, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
			SkyKeeperEntity::canSpawnOn );

		FlyingPhantomStructure.setupSeparationSettings();
		DimensionStructuresSettings.field_236191_b_ = ImmutableMap.<Structure<?>, StructureSeparationSettings>builder()
			.putAll(DimensionStructuresSettings.field_236191_b_)
			.put( FlyingPhantomStructure.INSTANCE, FlyingPhantomStructure.SEPARATION_SETTINGS )
			.build();
		DimensionSettings.field_242740_q.getStructures().field_236193_d_.put( FlyingPhantomStructure.INSTANCE, FlyingPhantomStructure.SEPARATION_SETTINGS );
	}

	private static void registerCommands( RegisterCommandsEvent event ) {
		CommandDispatcher< CommandSource > dispatcher = event.getDispatcher();

		ChangeGameStateCommand.register( dispatcher );
		StopUndeadArmyCommand.register( dispatcher );
	}

	private static void onServerStart( FMLServerStartingEvent event ) {
		MinecraftServer server = event.getServer();
		UNDEAD_ARMY_MANAGER.updateWorld( server.func_241755_D_() );

		TreasureBagManager.addTreasureBagTo( EntityType.ELDER_GUARDIAN, Instances.TreasureBags.ELDER_GUARDIAN, true );
		TreasureBagManager.addTreasureBagTo( EntityType.WITHER, Instances.TreasureBags.WITHER, false );
		TreasureBagManager.addTreasureBagTo( EntityType.ENDER_DRAGON, Instances.TreasureBags.ENDER_DRAGON, true );
	}

	public static void onLoadingWorld( WorldEvent.Load event ) {
		if( !( event.getWorld() instanceof ServerWorld ) )
			return;

		ServerWorld world = ( ServerWorld )event.getWorld();
		DimensionSavedDataManager manager = world.getSavedData();

		UNDEAD_ARMY_MANAGER = manager.getOrCreate( ()->new UndeadArmyManager( world ), UndeadArmyManager.DATA_NAME );
		UNDEAD_ARMY_MANAGER.updateWorld( world );

		GAME_DATA_SAVER = manager.getOrCreate( GameDataSaver::new, GameDataSaver.DATA_NAME );

		ReloadUndeadArmyGoals.resetTimer();
	}

	public static void onSavingWorld( WorldEvent.Save event ) {
		if( !( event.getWorld() instanceof ServerWorld ) )
			return;

		GAME_DATA_SAVER.markDirty();
		UNDEAD_ARMY_MANAGER.markDirty();
	}
}
