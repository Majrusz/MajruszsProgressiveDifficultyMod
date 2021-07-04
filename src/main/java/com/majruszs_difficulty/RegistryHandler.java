package com.majruszs_difficulty;

import com.majruszs_difficulty.commands.ChangeGameStateCommand;
import com.majruszs_difficulty.commands.GetDifficultyCommand;
import com.majruszs_difficulty.commands.UndeadArmyManagerCommand;
import com.majruszs_difficulty.entities.*;
import com.majruszs_difficulty.features.treasure_bag.TreasureBagManager;
import com.majruszs_difficulty.features.undead_army.ReloadUndeadArmyGoals;
import com.majruszs_difficulty.features.undead_army.UndeadArmyManager;
import com.majruszs_difficulty.generation.OreGeneration;
import com.majruszs_difficulty.items.AttributeArmorItem;
import com.majruszs_difficulty.items.FakeItem;
import com.mlib.items.SpawnEggFactory;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
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
	public static final DeferredRegister< Block > BLOCKS = DeferredRegister.create( ForgeRegistries.BLOCKS, MajruszsDifficulty.MOD_ID );
	public static final DeferredRegister< Item > ITEMS = DeferredRegister.create( ForgeRegistries.ITEMS, MajruszsDifficulty.MOD_ID );
	public static final DeferredRegister< SoundEvent > SOUNDS = DeferredRegister.create( ForgeRegistries.SOUND_EVENTS, MajruszsDifficulty.MOD_ID );
	public static final DeferredRegister< Effect > EFFECTS = DeferredRegister.create( ForgeRegistries.POTIONS, MajruszsDifficulty.MOD_ID );
	public static final DeferredRegister< Structure< ? > > STRUCTURES = DeferredRegister.create( ForgeRegistries.STRUCTURE_FEATURES,
		MajruszsDifficulty.MOD_ID
	);
	public static final DeferredRegister< ParticleType< ? > > PARTICLES = DeferredRegister.create( ForgeRegistries.PARTICLE_TYPES,
		MajruszsDifficulty.MOD_ID
	);

	public static UndeadArmyManager UNDEAD_ARMY_MANAGER;
	public static GameDataSaver GAME_DATA_SAVER;

	/** Main method to initialize everything. */
	public static void init() {
		FMLJavaModLoadingContext loadingContext = FMLJavaModLoadingContext.get();
		final IEventBus modEventBus = loadingContext.getModEventBus();

		registerEverything( modEventBus );
		modEventBus.addListener( RegistryHandler::setup );
		modEventBus.addListener( RegistryHandler::setupClient );
		DistExecutor.unsafeRunWhenOn( Dist.CLIENT, ()->()->modEventBus.addListener( RegistryHandler::onTextureStitch ) );

		IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
		forgeEventBus.addListener( RegistryHandler::onLoadingWorld );
		forgeEventBus.addListener( RegistryHandler::onSavingWorld );
		forgeEventBus.addListener( RegistryHandler::onServerStart );
		forgeEventBus.addListener( RegistryHandler::registerCommands );
	}

	/** Registration of entities. */
	private static void registerEntities( final IEventBus modEventBus ) {
		ENTITIES.register( "giant", ()->GiantEntity.type );
		ENTITIES.register( "pillager_wolf", ()->PillagerWolfEntity.type );
		ENTITIES.register( "elite_skeleton", ()->EliteSkeletonEntity.type );
		ENTITIES.register( "sky_keeper", ()->SkyKeeperEntity.type );
		ENTITIES.register( "creeperling", ()->CreeperlingEntity.type );
		ENTITIES.register( "parasite", ()->ParasiteEntity.type );
		ENTITIES.register( modEventBus );
	}

	/** Registration of entity spawn eggs. */
	private static void registerSpawnEggs() {
		SpawnEggFactory.setDefaultItemGroup( Instances.ITEM_GROUP );
		SpawnEggFactory.createRegistrySpawnEgg( ITEMS, "giant_spawn_egg", GiantEntity.type, 44975, 7969893 );
		SpawnEggFactory.createRegistrySpawnEgg( ITEMS, "pillager_wolf_spawn_egg", PillagerWolfEntity.type, 9804699, 5451574 );
		SpawnEggFactory.createRegistrySpawnEgg( ITEMS, "illusioner_spawn_egg", EntityType.ILLUSIONER, 0x135a97, 9804699 );
		SpawnEggFactory.createRegistrySpawnEgg( ITEMS, "elite_skeleton_spawn_egg", EliteSkeletonEntity.type, 12698049, 0xFE484D );
		SpawnEggFactory.createRegistrySpawnEgg( ITEMS, "sky_keeper_spawn_egg", SkyKeeperEntity.type, 0x7B45AD, 0xF0F0F0 );
		SpawnEggFactory.createRegistrySpawnEgg( ITEMS, "creeperling_spawn_egg", CreeperlingEntity.type, 0x0DA70B, 0x000000 );
		SpawnEggFactory.createRegistrySpawnEgg( ITEMS, "parasite_spawn_egg", ParasiteEntity.type, 0x161616, 0x946794 );
	}

	/** Registers list of fake items. */
	private static void registerFakeItems( String ...registerNames ) {
		for( String registerName : registerNames )
			ITEMS.register( "advancement_" + registerName, FakeItem::new );
	}

	/** Registration of all fake items. (custom icons) */
	private static void registerFakeItems() {
		registerFakeItems( "normal", "expert", "master", "bleeding" );
	}

	/** Registration of treasure bags. */
	private static void registerTreasureBags() {
		Instances.UNDEAD_ARMY_TREASURE_BAG.register();
		Instances.ELDER_GUARDIAN_TREASURE_BAG.register();
		Instances.WITHER_TREASURE_BAG.register();
		Instances.ENDER_DRAGON_TREASURE_BAG.register();
		Instances.FISHING_TREASURE_BAG.register();
		Instances.PILLAGER_TREASURE_BAG.register();
	}

	/** Registration of items. */
	private static void registerItems( final IEventBus modEventBus ) {
		ITEMS.register( "wither_sword", ()->Instances.WITHER_SWORD );
		ITEMS.register( "hermes_boots", ()->Instances.HERMES_BOOTS_ITEM );
		ITEMS.register( "end_shard", ()->Instances.END_SHARD_ITEM );
		ITEMS.register( "end_ingot", ()->Instances.END_INGOT_ITEM );
		ITEMS.register( "end_sword", ()->Instances.END_SWORD_ITEM );
		ITEMS.register( "end_shovel", ()->Instances.END_SHOVEL_ITEM );
		ITEMS.register( "end_pickaxe", ()->Instances.END_PICKAXE_ITEM );
		ITEMS.register( "end_axe", ()->Instances.END_AXE_ITEM );
		ITEMS.register( "end_hoe", ()->Instances.END_HOE_ITEM );
		ITEMS.register( "end_helmet", ()->Instances.END_HELMET_ITEM );
		ITEMS.register( "end_chestplate", ()->Instances.END_CHESTPLATE_ITEM );
		ITEMS.register( "end_leggings", ()->Instances.END_LEGGINGS_ITEM );
		ITEMS.register( "end_boots", ()->Instances.END_BOOTS_ITEM );
		ITEMS.register( "end_shard_locator", ()->Instances.END_SHARD_LOCATOR_ITEM );
		ITEMS.register( "tattered_cloth", ()->Instances.TATTERED_CLOTH_ITEM );
		ITEMS.register( "undead_battle_standard", ()->Instances.BATTLE_STANDARD_ITEM );
		ITEMS.register( "bandage", ()->Instances.BANDAGE_ITEM );
		ITEMS.register( "golden_bandage", ()->Instances.GOLDEN_BANDAGE_ITEM );
		ITEMS.register( "recall_potion", ()->Instances.RECALL_POTION_ITEM );
		ITEMS.register( "ocean_shield", ()->Instances.OCEAN_SHIELD_ITEM );
		registerTreasureBags();
		registerSpawnEggs();
		registerFakeItems();
		ITEMS.register( modEventBus );
	}

	/** Registration of blocks. */
	private static void registerBlocks( final IEventBus modEventBus ) {
		BLOCKS.register( "end_shard_ore", ()->Instances.END_SHARD_ORE );
		ITEMS.register( "end_shard_ore", ()->Instances.END_SHARD_ORE_ITEM );
		BLOCKS.register( "end_block", ()->Instances.END_BLOCK );
		ITEMS.register( "end_block", ()->Instances.END_BLOCK_ITEM );
		BLOCKS.register( "infested_end_stone", ()->Instances.INFESTED_END_STONE );
		ITEMS.register( "infested_end_stone", ()->Instances.INFESTED_END_STONE_ITEM );
		BLOCKS.register( modEventBus );
	}

	/** Registration of sounds. */
	private static void registerSounds( final IEventBus modEventBus ) {
		SOUNDS.register( "undead_army.approaching", ()->Instances.Sounds.UNDEAD_ARMY_APPROACHING );
		SOUNDS.register( "undead_army.wave_started", ()->Instances.Sounds.UNDEAD_ARMY_WAVE_STARTED );
		SOUNDS.register( modEventBus );
	}

	/** Registration of effects. */
	private static void registerEffects( final IEventBus modEventBus ) {
		EFFECTS.register( "bleeding", ()->Instances.BLEEDING );
		EFFECTS.register( "bleeding_immunity", ()->Instances.BLEEDING_IMMUNITY );
		EFFECTS.register( "infested", ()->Instances.INFESTED );
		EFFECTS.register( modEventBus );
	}

	/** Registration of particles. */
	private static void registerParticles( final IEventBus modEventBus ) {
		PARTICLES.register( "blood_particle", ()->Instances.BLOOD_PARTICLE );
		PARTICLES.register( modEventBus );
	}

	/** Registration of structures. */
	private static void registerStructures( final IEventBus modEventBus ) {
		STRUCTURES.register( "flying_phantom_structure", ()->Instances.FLYING_PHANTOM );
		STRUCTURES.register( "flying_end_island", ()->Instances.FLYING_END_ISLAND );
		STRUCTURES.register( "flying_end_ship", ()->Instances.FLYING_END_SHIP );
		STRUCTURES.register( modEventBus );

		Structure.NAME_STRUCTURE_BIMAP.put( "flying_phantom_structure", Instances.FLYING_PHANTOM );
		Structure.NAME_STRUCTURE_BIMAP.put( "flying_end_island", Instances.FLYING_END_ISLAND );
		Structure.NAME_STRUCTURE_BIMAP.put( "flying_end_ship", Instances.FLYING_END_SHIP );
	}

	/** Registration of everything. */
	private static void registerEverything( final IEventBus modEventBus ) {
		registerEntities( modEventBus );
		registerBlocks( modEventBus );
		registerItems( modEventBus );
		registerSounds( modEventBus );
		registerEffects( modEventBus );
		registerParticles( modEventBus );
		registerStructures( modEventBus );
	}

	/** Setting up client models etc. */
	private static void setupClient( final FMLClientSetupEvent event ) {
		RegistryHandlerClient.setup();
	}

	/** Setting up entities and structures. */
	private static void setup( final FMLCommonSetupEvent event ) {
		GlobalEntityTypeAttributes.put( GiantEntity.type, GiantEntity.getAttributeMap() );
		GlobalEntityTypeAttributes.put( PillagerWolfEntity.type, PillagerWolfEntity.getAttributeMap() );
		GlobalEntityTypeAttributes.put( EliteSkeletonEntity.type, EliteSkeletonEntity.getAttributeMap() );
		GlobalEntityTypeAttributes.put( SkyKeeperEntity.type, SkyKeeperEntity.getAttributeMap() );
		GlobalEntityTypeAttributes.put( CreeperlingEntity.type, CreeperlingEntity.getAttributeMap() );
		GlobalEntityTypeAttributes.put( ParasiteEntity.type, ParasiteEntity.getAttributeMap() );

		EntitySpawnPlacementRegistry.register( GiantEntity.type, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
			Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GiantEntity::canMonsterSpawnInLight
		);
		EntitySpawnPlacementRegistry.register( PillagerWolfEntity.type, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
			Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, PillagerWolfEntity::canAnimalSpawn
		);
		EntitySpawnPlacementRegistry.register( EliteSkeletonEntity.type, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
			Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EliteSkeletonEntity::canMonsterSpawnInLight
		);
		EntitySpawnPlacementRegistry.register( SkyKeeperEntity.type, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
			Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SkyKeeperEntity::canSpawnOn
		);
		EntitySpawnPlacementRegistry.register( CreeperlingEntity.type, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
			Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, CreeperlingEntity::canSpawnOn
		);
		EntitySpawnPlacementRegistry.register( ParasiteEntity.type, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
			Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ParasiteEntity::canMonsterSpawnInLight
		);

		event.enqueueWork( Instances.FLYING_PHANTOM::setup );
		event.enqueueWork( Instances.FLYING_END_ISLAND::setup );
		event.enqueueWork( Instances.FLYING_END_SHIP::setup );
		OreGeneration.registerOres();

		AttributeArmorItem.updateAllItemsAttributes();
	}

	/** Registration of commands. */
	private static void registerCommands( RegisterCommandsEvent event ) {
		CommandDispatcher< CommandSource > dispatcher = event.getDispatcher();

		ChangeGameStateCommand.register( dispatcher );
		UndeadArmyManagerCommand.register( dispatcher );
		GetDifficultyCommand.register( dispatcher );
	}

	/**
	 *
	 */
	private static void onServerStart( FMLServerStartingEvent event ) {
		MinecraftServer server = event.getServer();
		UNDEAD_ARMY_MANAGER.updateWorld( server.func_241755_D_() );

		TreasureBagManager.addTreasureBagTo( EntityType.ELDER_GUARDIAN, Instances.ELDER_GUARDIAN_TREASURE_BAG, false );
		TreasureBagManager.addTreasureBagTo( EntityType.WITHER, Instances.WITHER_TREASURE_BAG, false );
		TreasureBagManager.addTreasureBagTo( EntityType.ENDER_DRAGON, Instances.ENDER_DRAGON_TREASURE_BAG, false );
	}

	/**
	 *
	 */
	public static void onLoadingWorld( WorldEvent.Load event ) {
		if( !( event.getWorld() instanceof ServerWorld ) )
			return;

		ServerWorld world = ( ServerWorld )event.getWorld();
		DimensionSavedDataManager manager = world.getSavedData();

		UNDEAD_ARMY_MANAGER = manager.getOrCreate( ()->new UndeadArmyManager( world ), UndeadArmyManager.DATA_NAME );
		UNDEAD_ARMY_MANAGER.updateWorld( world );

		GAME_DATA_SAVER = manager.getOrCreate( GameDataSaver::new, GameDataSaver.DATA_NAME );
		GAME_DATA_SAVER.updateGameState();

		ReloadUndeadArmyGoals.resetTimer();

		if( event.getWorld() instanceof ServerWorld ) {
			ServerWorld serverWorld = ( ServerWorld )event.getWorld();

			if( serverWorld.getChunkProvider()
				.getChunkGenerator() instanceof FlatChunkGenerator && serverWorld.getDimensionKey()
				.equals( World.OVERWORLD ) ) {
				return;
			}

			Map< Structure< ? >, StructureSeparationSettings > tempMap = new HashMap<>( serverWorld.getChunkProvider().generator.func_235957_b_()
				.func_236195_a_() );
			tempMap.putIfAbsent( Instances.FLYING_PHANTOM, DimensionStructuresSettings.field_236191_b_.get( Instances.FLYING_PHANTOM ) );
			tempMap.putIfAbsent( Instances.FLYING_END_ISLAND, DimensionStructuresSettings.field_236191_b_.get( Instances.FLYING_END_ISLAND ) );
			tempMap.putIfAbsent( Instances.FLYING_END_SHIP, DimensionStructuresSettings.field_236191_b_.get( Instances.FLYING_END_SHIP ) );
			serverWorld.getChunkProvider().generator.func_235957_b_().field_236193_d_ = tempMap;
		}
	}

	/**
	 *
	 */
	public static void onSavingWorld( WorldEvent.Save event ) {
		if( !( event.getWorld() instanceof ServerWorld ) )
			return;

		GAME_DATA_SAVER.markDirty();
		UNDEAD_ARMY_MANAGER.markDirty();
	}

	/** Adds custom textures to the game. (curios slot) */
	@OnlyIn( Dist.CLIENT )
	private static void onTextureStitch( TextureStitchEvent.Pre event ) {
		final AtlasTexture map = event.getMap();
		if( PlayerContainer.LOCATION_BLOCKS_TEXTURE.equals( map.getTextureLocation() ) )
			event.addSprite( RegistryHandlerClient.OCEAN_SHIELD_MATERIAL.getTextureLocation() );
	}
}
