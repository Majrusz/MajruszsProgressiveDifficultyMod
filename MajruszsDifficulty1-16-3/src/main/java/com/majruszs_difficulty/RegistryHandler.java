package com.majruszs_difficulty;

import com.majruszs_difficulty.commands.ChangeGameStateCommand;
import com.majruszs_difficulty.entities.EliteSkeletonEntity;
import com.majruszs_difficulty.entities.GiantEntity;
import com.majruszs_difficulty.entities.PillagerWolfEntity;
import com.majruszs_difficulty.events.UndeadArmy;
import com.majruszs_difficulty.events.undead_army.UndeadArmyManager;
import com.majruszs_difficulty.items.UndeadBattleStandard;
import com.majruszs_difficulty.items.WitherSwordItem;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.raid.RaidManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {
	public static final DeferredRegister< EntityType< ? > > ENTITIES = DeferredRegister.create( ForgeRegistries.ENTITIES, MajruszsDifficulty.MOD_ID );
	public static final DeferredRegister< Item > ITEMS = DeferredRegister.create( ForgeRegistries.ITEMS, MajruszsDifficulty.MOD_ID );
	public static final DeferredRegister< SoundEvent > SOUNDS = DeferredRegister.create( ForgeRegistries.SOUND_EVENTS, MajruszsDifficulty.MOD_ID );

	public static final ItemGroup ITEM_GROUP = new CustomItemGroup( "majruszs_tab" );

	// Entities
	public static final RegistryObject< EntityType< GiantEntity > > GIANT = ENTITIES.register( "giant", ()->GiantEntity.type );
	public static final RegistryObject< EntityType< PillagerWolfEntity > > PILLAGER_WOLF = ENTITIES.register( "pillager_wolf",
		()->PillagerWolfEntity.type
	);
	public static final RegistryObject< EntityType< EliteSkeletonEntity > > ELITE_SKELETON = ENTITIES.register( "elite_skeleton",
		()->EliteSkeletonEntity.type
	);

	// Items
	public static final RegistryObject< SwordItem > WITHER_SWORD = ITEMS.register( "wither_sword", WitherSwordItem::new );
	public static final RegistryObject< Item > UNDEAD_BATTLE_STANDARD = ITEMS.register( "undead_battle_standard", UndeadBattleStandard::new );

	// Sounds
	public static final RegistryObject< SoundEvent > UNDEAD_ARMY_APPROACHING = SOUNDS.register( "undead_army_approaching", ()->new SoundEvent(
		new ResourceLocation( MajruszsDifficulty.MOD_ID, "undead_army_approaching" ) )
	);

	public static UndeadArmyManager undeadArmyManager;

	public static void init() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get()
			.getModEventBus();

		registerObjects( modEventBus );
		modEventBus.addListener( RegistryHandler::setup );
		modEventBus.addListener( RegistryHandler::doClientSetup );

		MinecraftForge.EVENT_BUS.addListener( RegistryHandler::onLoadingWorld );
		MinecraftForge.EVENT_BUS.addListener( RegistryHandler::onSavingWorld );
		MinecraftForge.EVENT_BUS.addListener( RegistryHandler::onServerStart );
		MinecraftForge.EVENT_BUS.addListener( RegistryHandler::registerCommands );
	}

	private static void registerObjects( final IEventBus modEventBus ) {
		NewSpawnEggs.registerSpawnEgg( "giant_spawn_egg", GiantEntity.type, 44975, 7969893 );
		NewSpawnEggs.registerSpawnEgg( "pillager_wolf_spawn_egg", PillagerWolfEntity.type, 9804699, 5451574 );
		NewSpawnEggs.registerSpawnEgg( "illusioner_spawn_egg", EntityType.ILLUSIONER, 0x135a97, 9804699 );
		NewSpawnEggs.registerSpawnEgg( "elite_skeleton_spawn_egg", EliteSkeletonEntity.type, 12698049, 0xFE484D );

		ENTITIES.register( modEventBus );
		ITEMS.register( modEventBus );
		SOUNDS.register( modEventBus );
	}

	private static void setup( final FMLCommonSetupEvent event ) {
		GlobalEntityTypeAttributes.put( GiantEntity.type, GiantEntity.getAttributeMap() );
		GlobalEntityTypeAttributes.put( PillagerWolfEntity.type, PillagerWolfEntity.getAttributeMap() );
		GlobalEntityTypeAttributes.put( EliteSkeletonEntity.type, EliteSkeletonEntity.getAttributeMap() );

		NewSpawnEggs.addDispenseBehaviorToAllRegisteredEggs();
	}

	private static void doClientSetup( final FMLClientSetupEvent event ) {
		RegistryHandlerClient.setup();
	}

	private static void registerCommands( RegisterCommandsEvent event ) {
		CommandDispatcher< CommandSource > dispatcher = event.getDispatcher();

		ChangeGameStateCommand.register( dispatcher );
	}

	private static void onServerStart( FMLServerStartingEvent event ) {
		MinecraftServer server = event.getServer();
		RegistryHandler.undeadArmyManager.updateWorld( server.func_241755_D_() );
	}

	public static void onLoadingWorld( WorldEvent.Load event ) {
		if( !( event.getWorld() instanceof ServerWorld ) )
			return;

		ServerWorld world = ( ServerWorld )event.getWorld();

		undeadArmyManager = world.getSavedData().getOrCreate( ()->new UndeadArmyManager( world ), UndeadArmyManager.DATA_NAME );
		DataSaver saver = DataSaver.getDataFor( world );

		if( saver.data.contains( "DifficultyState" ) ) {
			GameState.changeMode( GameState.convertIntegerToMode( saver.data.getInt( "DifficultyState" ) ) );
		}
	}

	public static void onSavingWorld( WorldEvent.Save event ) {
		if( !( event.getWorld() instanceof ServerWorld ) )
			return;

		ServerWorld world = ( ServerWorld )event.getWorld();

		if( world.isRemote )
			return;

		DataSaver saver = DataSaver.getDataFor( world );
		CompoundNBT data = new CompoundNBT();
		data.putInt( "DifficultyState", GameState.convertModeToInteger( GameState.getCurrentMode() ) );
		saver.data = data;
		saver.markDirty();

		undeadArmyManager.markDirty();
	}
}
