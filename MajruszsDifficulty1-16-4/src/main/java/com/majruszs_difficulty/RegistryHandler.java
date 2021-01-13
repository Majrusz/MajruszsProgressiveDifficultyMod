package com.majruszs_difficulty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.majruszs_difficulty.commands.ChangeGameStateCommand;
import com.majruszs_difficulty.commands.StopUndeadArmyCommand;
import com.majruszs_difficulty.effects.BleedingEffect;
import com.majruszs_difficulty.entities.EliteSkeletonEntity;
import com.majruszs_difficulty.entities.GiantEntity;
import com.majruszs_difficulty.entities.PillagerWolfEntity;
import com.majruszs_difficulty.entities.SkyKeeperEntity;
import com.majruszs_difficulty.events.treasure_bag.TreasureBagManager;
import com.majruszs_difficulty.events.undead_army.ReloadUndeadArmyGoals;
import com.majruszs_difficulty.events.undead_army.UndeadArmyManager;
import com.majruszs_difficulty.items.BandageItem;
import com.majruszs_difficulty.items.TreasureBagItem;
import com.majruszs_difficulty.items.UndeadBattleStandardItem;
import com.majruszs_difficulty.items.WitherSwordItem;
import com.majruszs_difficulty.structure_pieces.FlyingPhantomPiece;
import com.majruszs_difficulty.structures.FlyingPhantomStructure;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SwordItem;
import net.minecraft.potion.Effect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.World;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

/** Main class registering most registers like entities, items and sounds. */
public class RegistryHandler {
	public static final DeferredRegister< EntityType< ? > > ENTITIES = DeferredRegister.create( ForgeRegistries.ENTITIES, MajruszsDifficulty.MOD_ID );
	public static final DeferredRegister< Item > ITEMS = DeferredRegister.create( ForgeRegistries.ITEMS, MajruszsDifficulty.MOD_ID );
	public static final DeferredRegister< SoundEvent > SOUNDS = DeferredRegister.create( ForgeRegistries.SOUND_EVENTS, MajruszsDifficulty.MOD_ID );
	public static final DeferredRegister< Effect > EFFECTS = DeferredRegister.create( ForgeRegistries.POTIONS, MajruszsDifficulty.MOD_ID );
	public static final DeferredRegister< Structure< ? > > STRUCTURES = DeferredRegister.create( ForgeRegistries.STRUCTURE_FEATURES, MajruszsDifficulty.MOD_ID );
	public static final ItemGroup ITEM_GROUP = new CustomItemGroup( "majruszs_tab" );

	// Entities
	public static final RegistryObject< EntityType< GiantEntity > > GIANT = ENTITIES.register( "giant", ()->GiantEntity.type );
	public static final RegistryObject< EntityType< PillagerWolfEntity > > PILLAGER_WOLF = ENTITIES.register( "pillager_wolf",
		()->PillagerWolfEntity.type
	);
	public static final RegistryObject< EntityType< EliteSkeletonEntity > > ELITE_SKELETON = ENTITIES.register( "elite_skeleton",
		()->EliteSkeletonEntity.type
	);
	public static final RegistryObject< EntityType< SkyKeeperEntity > > SKY_KEEPER = ENTITIES.register( "sky_keeper", ()->SkyKeeperEntity.type );

	// Items
	public static final RegistryObject< SwordItem > WITHER_SWORD = ITEMS.register( "wither_sword", WitherSwordItem::new );
	public static final RegistryObject< Item > UNDEAD_BATTLE_STANDARD = ITEMS.register( "undead_battle_standard", UndeadBattleStandardItem::new );
	public static final RegistryObject< TreasureBagItem > UNDEAD_TREASURE_BAG = TreasureBagItem.getRegistry( "undead_army" );
	public static final RegistryObject< TreasureBagItem > ELDER_GUARDIAN_TREASURE_BAG = TreasureBagItem.getRegistry( "elder_guardian" );
	public static final RegistryObject< TreasureBagItem > WITHER_TREASURE_BAG = TreasureBagItem.getRegistry( "wither" );
	public static final RegistryObject< TreasureBagItem > ENDER_DRAGON_TREASURE_BAG = TreasureBagItem.getRegistry( "ender_dragon" );
	public static final RegistryObject< TreasureBagItem > FISHING_TREASURE_BAG = TreasureBagItem.getRegistry( "fishing" );
	public static final RegistryObject< BandageItem > BANDAGE = ITEMS.register( "bandage", BandageItem::new );

	// Sounds
	public static final RegistryObject< SoundEvent > UNDEAD_ARMY_APPROACHING = SOUNDS.register( "undead_army.approaching",
		()->new SoundEvent( new ResourceLocation( MajruszsDifficulty.MOD_ID, "undead_army.approaching" ) )
	);
	public static final RegistryObject< SoundEvent > UNDEAD_ARMY_WAVE_STARTED = SOUNDS.register( "undead_army.wave_started",
		()->new SoundEvent( new ResourceLocation( MajruszsDifficulty.MOD_ID, "undead_army.wave_started" ) )
	);

	// Effects
	public static final RegistryObject< Effect > BLEEDING = EFFECTS.register( "bleeding", ()->BleedingEffect.instance );

	// Damage sources
	public static final DamageSource BLEEDING_SOURCE = new DamageSource( "bleeding" ).setDamageBypassesArmor();

	// Structures
	public static final RegistryObject< Structure< NoFeatureConfig > > FLYING_PHANTOM = STRUCTURES.register( "flying_phantom_structure", ()->FlyingPhantomStructure.INSTANCE );

	// Structure pieces
	public static IStructurePieceType FLYING_PHANTOM_PIECE = IStructurePieceType.register( FlyingPhantomPiece::new, MajruszsHelper.getResource( "flying_phantom" ).toString() );

	public static UndeadArmyManager undeadArmyManager;
	public static GameDataSaver gameDataSaver = new GameDataSaver();

	public static void init() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get()
			.getModEventBus();

		registerObjects( modEventBus );
		modEventBus.addListener( RegistryHandler::setup );
		modEventBus.addListener( RegistryHandler::doClientSetup );

		IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
		forgeEventBus.addListener( RegistryHandler::onLoadingWorld );
		forgeEventBus.addListener( RegistryHandler::onSavingWorld );
		forgeEventBus.addListener( RegistryHandler::onServerStart );
		forgeEventBus.addListener( RegistryHandler::registerCommands );
	}

	private static void registerObjects( final IEventBus modEventBus ) {
		NewSpawnEggs.registerSpawnEgg( "giant_spawn_egg", GiantEntity.type, 44975, 7969893 );
		NewSpawnEggs.registerSpawnEgg( "pillager_wolf_spawn_egg", PillagerWolfEntity.type, 9804699, 5451574 );
		NewSpawnEggs.registerSpawnEgg( "illusioner_spawn_egg", EntityType.ILLUSIONER, 0x135a97, 9804699 );
		NewSpawnEggs.registerSpawnEgg( "elite_skeleton_spawn_egg", EliteSkeletonEntity.type, 12698049, 0xFE484D );
		NewSpawnEggs.registerSpawnEgg( "sky_keeper_spawn_egg", SkyKeeperEntity.type, 0x7B45AD, 0xF0F0F0 );

		ENTITIES.register( modEventBus );
		ITEMS.register( modEventBus );
		SOUNDS.register( modEventBus );
		EFFECTS.register( modEventBus );
		STRUCTURES.register( modEventBus );

		Structure.NAME_STRUCTURE_BIMAP.put( "flying_phantom_structure", FlyingPhantomStructure.INSTANCE );
	}

	private static void setup( final FMLCommonSetupEvent event ) {
		GlobalEntityTypeAttributes.put( GiantEntity.type, GiantEntity.getAttributeMap() );
		GlobalEntityTypeAttributes.put( PillagerWolfEntity.type, PillagerWolfEntity.getAttributeMap() );
		GlobalEntityTypeAttributes.put( EliteSkeletonEntity.type, EliteSkeletonEntity.getAttributeMap() );
		GlobalEntityTypeAttributes.put( SkyKeeperEntity.type, SkyKeeperEntity.getAttributeMap() );

		NewSpawnEggs.addDispenseBehaviorToAllRegisteredEggs();

		EntitySpawnPlacementRegistry.register( GiantEntity.type, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
			GiantEntity::canMonsterSpawn );
		EntitySpawnPlacementRegistry.register( PillagerWolfEntity.type, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
			PillagerWolfEntity::canAnimalSpawn );
		EntitySpawnPlacementRegistry.register( EliteSkeletonEntity.type, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
			EliteSkeletonEntity::canMonsterSpawn );
		EntitySpawnPlacementRegistry.register( SkyKeeperEntity.type, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
			SkyKeeperEntity::canSpawnOn );
		// DungeonHooks.addDungeonMob( EliteSkeletonEntity.type, 20 );
		DimensionStructuresSettings.field_236191_b_ = ImmutableMap.<Structure<?>, StructureSeparationSettings>builder()
			.putAll(DimensionStructuresSettings.field_236191_b_)
			.put( FlyingPhantomStructure.INSTANCE, FlyingPhantomStructure.SEPARATION_SETTINGS )
			.build();
		DimensionSettings.field_242740_q.getStructures().field_236193_d_.put( FlyingPhantomStructure.INSTANCE, FlyingPhantomStructure.SEPARATION_SETTINGS );
	}

	private static void doClientSetup( final FMLClientSetupEvent event ) {
		RegistryHandlerClient.setup();
	}

	private static void registerCommands( RegisterCommandsEvent event ) {
		CommandDispatcher< CommandSource > dispatcher = event.getDispatcher();

		ChangeGameStateCommand.register( dispatcher );
		StopUndeadArmyCommand.register( dispatcher );
	}

	private static void onServerStart( FMLServerStartingEvent event ) {
		MinecraftServer server = event.getServer();
		undeadArmyManager.updateWorld( server.func_241755_D_() );

		TreasureBagManager.addTreasureBagTo( EntityType.ELDER_GUARDIAN, ELDER_GUARDIAN_TREASURE_BAG.get(), true );
		TreasureBagManager.addTreasureBagTo( EntityType.WITHER, WITHER_TREASURE_BAG.get(), false );
		TreasureBagManager.addTreasureBagTo( EntityType.ENDER_DRAGON, ENDER_DRAGON_TREASURE_BAG.get(), true );
	}

	public static void onLoadingWorld( WorldEvent.Load event ) {
		if( !( event.getWorld() instanceof ServerWorld ) )
			return;

		ServerWorld world = ( ServerWorld )event.getWorld();
		DimensionSavedDataManager manager = world.getSavedData();

		undeadArmyManager = manager.getOrCreate( ()->new UndeadArmyManager( world ), UndeadArmyManager.DATA_NAME );
		undeadArmyManager.updateWorld( world );

		gameDataSaver = manager.getOrCreate( GameDataSaver::new, GameDataSaver.DATA_NAME );

		ReloadUndeadArmyGoals.resetTimer();
	}

	public static void onSavingWorld( WorldEvent.Save event ) {
		if( !( event.getWorld() instanceof ServerWorld ) )
			return;

		gameDataSaver.markDirty();
		undeadArmyManager.markDirty();
	}
}
