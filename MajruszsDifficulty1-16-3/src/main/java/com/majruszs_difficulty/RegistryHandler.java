package com.majruszs_difficulty;

import com.majruszs_difficulty.commands.ChangeGameStateCommand;
import com.majruszs_difficulty.entities.GiantEntity;
import com.majruszs_difficulty.entities.PillagerWolfEntity;
import com.majruszs_difficulty.renderers.GiantRenderer;
import com.majruszs_difficulty.renderers.PillagerWolfRenderer;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.block.DispenserBlock;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {
	public static final DeferredRegister< EntityType< ? > > ENTITY_TYPES = DeferredRegister.create( ForgeRegistries.ENTITIES,
		MajruszsDifficulty.MOD_ID
	);
	public static final DeferredRegister< Item > ITEMS = DeferredRegister.create( ForgeRegistries.ITEMS, MajruszsDifficulty.MOD_ID );

	public static final RegistryObject< EntityType< GiantEntity > > GIANT = ENTITY_TYPES.register( "giant", GiantEntity::buildEntity );

	public static final RegistryObject< EntityType< PillagerWolfEntity > > PILLAGER_WOLF = ENTITY_TYPES.register( "pillager_wolf",
		PillagerWolfEntity::buildEntity
	);

	public static final RegistryObject< SpawnEggItem > GIANT_SPAWN_EGG = ITEMS.register( "giant_spawn_egg",
		()->new SpawnEggItem( GiantEntity.type, 44975, 7969893, new Item.Properties().group( ItemGroup.MISC ) )
	);

	public static final RegistryObject< SpawnEggItem > PILLAGER_WOLF_SPAWN_EGG = ITEMS.register( "pillager_wolf_spawn_egg",
		()->new SpawnEggItem( PillagerWolfEntity.type, 9804699, 5451574, new Item.Properties().group( ItemGroup.MISC ) )
	);

	public static final RegistryObject< SpawnEggItem > ILLUSIONER_SPAWN_EGG = ITEMS.register( "illusioner_spawn_egg",
		()->new SpawnEggItem( EntityType.ILLUSIONER, 0x135a97, 9804699, new Item.Properties().group( ItemGroup.MISC ) )
	);

	public static void init() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get()
			.getModEventBus();

		registerObjects( modEventBus );
		modEventBus.addListener( RegistryHandler::setup );
		modEventBus.addListener( RegistryHandler::doClientSetup );

		MinecraftForge.EVENT_BUS.addListener( RegistryHandler::onLoadingWorld );
		MinecraftForge.EVENT_BUS.addListener( RegistryHandler::onSavingWorld );
		MinecraftForge.EVENT_BUS.addListener( RegistryHandler::onServerStart );
	}

	private static void registerObjects( final IEventBus modEventBus ) {
		ENTITY_TYPES.register( modEventBus );
		ITEMS.register( modEventBus );
	}

	private static void setup( final FMLCommonSetupEvent event ) {
		GlobalEntityTypeAttributes.put( GiantEntity.type, GiantEntity.setAttributes()
			.func_233813_a_() );
		GlobalEntityTypeAttributes.put( PillagerWolfEntity.type, PillagerWolfEntity.setAttributes()
			.func_233813_a_() );

		DefaultDispenseItemBehavior defaultBehavior = new DefaultDispenseItemBehavior() {
			public ItemStack dispenseStack( IBlockSource source, ItemStack stack ) {
				Direction direction = source.getBlockState()
					.get( DispenserBlock.FACING );
				EntityType< ? > entityType = ( ( SpawnEggItem )stack.getItem() ).getType( stack.getTag() );
				entityType.spawn( source.getWorld(), stack, ( PlayerEntity )null, source.getBlockPos()
					.offset( direction ), SpawnReason.DISPENSER, direction != Direction.UP, false );
				stack.shrink( 1 );
				return stack;
			}
		};

		for( SpawnEggItem spawnEgg : new SpawnEggItem[]{ GIANT_SPAWN_EGG.get(), ILLUSIONER_SPAWN_EGG.get(), PILLAGER_WOLF_SPAWN_EGG.get() } ) {
			DispenserBlock.registerDispenseBehavior( spawnEgg, defaultBehavior );
		}
	}

	private static void doClientSetup( final FMLClientSetupEvent event ) {
		RegistryHandlerClient.setup();
	}

	private static void onServerStart( FMLServerStartingEvent event ) {
		MinecraftServer server = event.getServer();
		Commands commands = server.getCommandManager();
		CommandDispatcher< CommandSource > dispatcher = commands.getDispatcher();

		ChangeGameStateCommand.register( dispatcher );
	}

	private static void onLoadingWorld( WorldEvent.Load event ) {
		if( !( event.getWorld() instanceof ServerWorld ) )
			return;

		ServerWorld world = ( ServerWorld )event.getWorld();

		if( world.isRemote )
			return;

		DataSaver saver = DataSaver.getDataFor( world );

		if( saver.data.contains( "DifficultyState" ) ) {
			GameState.changeMode( GameState.convertIntegerToMode( saver.data.getInt( "DifficultyState" ) ) );
		}
	}

	private static void onSavingWorld( WorldEvent.Save event ) {
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
	}
}
