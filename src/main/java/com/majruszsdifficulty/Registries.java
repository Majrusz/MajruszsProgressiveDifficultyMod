package com.majruszsdifficulty;

import com.majruszsdifficulty.blocks.EndShardOre;
import com.majruszsdifficulty.blocks.EnderiumBlock;
import com.majruszsdifficulty.blocks.InfestedEndStone;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.majruszsdifficulty.effects.BleedingImmunityEffect;
import com.majruszsdifficulty.entities.*;
import com.majruszsdifficulty.items.*;
import com.majruszsdifficulty.loot.CurseRandomlyFunction;
import com.majruszsdifficulty.treasurebags.TreasureBagManager;
import com.majruszsdifficulty.treasurebags.TreasureBagProgressManager;
import com.majruszsdifficulty.triggers.BandageTrigger;
import com.majruszsdifficulty.triggers.GameStageTrigger;
import com.majruszsdifficulty.triggers.TreasureBagTrigger;
import com.majruszsdifficulty.undeadarmy.UndeadArmyManager;
import com.mlib.Utility;
import com.mlib.annotations.AnnotationHandler;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.items.CreativeModeTabHelper;
import com.mlib.registries.RegistryHelper;
import com.mlib.triggers.BasicTrigger;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.majruszsdifficulty.MajruszsDifficulty.MOD_ID;
import static com.majruszsdifficulty.MajruszsDifficulty.SERVER_CONFIG;
import static net.minecraft.core.Registry.LOOT_FUNCTION_REGISTRY;
import static net.minecraft.core.Registry.LOOT_FUNCTION_TYPE;

public class Registries {
	private static final RegistryHelper HELPER = new RegistryHelper( MajruszsDifficulty.MOD_ID );

	static {
		GameModifier.addNewGroup( SERVER_CONFIG, Modifiers.DEFAULT ).name( "GameModifiers" );
		GameModifier.addNewGroup( SERVER_CONFIG, Modifiers.UNDEAD_ARMY ).name( "UndeadArmy" );
		GameModifier.addNewGroup( SERVER_CONFIG, Modifiers.GAME_STAGE ).name( "GameStage" );
		GameModifier.addNewGroup( SERVER_CONFIG, Modifiers.TREASURE_BAG ).name( "TreasureBag" );
		GameModifier.addNewGroup( SERVER_CONFIG, Modifiers.MOBS ).name( "Mobs" );
	}

	// Groups
	static final DeferredRegister< Block > BLOCKS = HELPER.create( ForgeRegistries.Keys.BLOCKS );
	static final DeferredRegister< Item > ITEMS = HELPER.create( ForgeRegistries.Keys.ITEMS );
	static final DeferredRegister< EntityType< ? > > ENTITY_TYPES = HELPER.create( ForgeRegistries.Keys.ENTITY_TYPES );
	static final DeferredRegister< MobEffect > MOB_EFFECTS = HELPER.create( ForgeRegistries.Keys.MOB_EFFECTS );
	static final DeferredRegister< ParticleType< ? > > PARTICLE_TYPES = HELPER.create( ForgeRegistries.Keys.PARTICLE_TYPES );
	static final DeferredRegister< SoundEvent > SOUNDS_EVENTS = HELPER.create( ForgeRegistries.Keys.SOUND_EVENTS );
	static final DeferredRegister< LootItemFunctionType > LOOT_FUNCTIONS = HELPER.create( LOOT_FUNCTION_REGISTRY );
	static final DeferredRegister< Potion > POTIONS = HELPER.create( ForgeRegistries.Keys.POTIONS );

	// Entities
	public static final RegistryObject< EntityType< CreeperlingEntity > > CREEPERLING = ENTITY_TYPES.register( "creeperling", CreeperlingEntity.createSupplier() );
	public static final RegistryObject< EntityType< TankEntity > > TANK = ENTITY_TYPES.register( "tank", TankEntity.createSupplier() );
	public static final RegistryObject< EntityType< CursedArmorEntity > > CURSED_ARMOR = ENTITY_TYPES.register( "cursed_armor", CursedArmorEntity.createSupplier() );
	public static final RegistryObject< EntityType< CerberusEntity > > CERBERUS = ENTITY_TYPES.register( "cerberus", CerberusEntity.createSupplier() );
	public static final RegistryObject< EntityType< BlackWidowEntity > > BLACK_WIDOW = ENTITY_TYPES.register( "black_widow", BlackWidowEntity.createSupplier() );

	// Items
	public static final RegistryObject< BandageItem > BANDAGE = ITEMS.register( "bandage", BandageItem::new );
	public static final RegistryObject< GoldenBandageItem > GOLDEN_BANDAGE = ITEMS.register( "golden_bandage", GoldenBandageItem::new );
	public static final RegistryObject< ClothItem > CLOTH = ITEMS.register( "cloth", ClothItem::new );
	public static final RegistryObject< UndeadBattleStandardItem > BATTLE_STANDARD = ITEMS.register( "undead_battle_standard", UndeadBattleStandardItem::new );
	public static final RegistryObject< TatteredArmorItem > TATTERED_HELMET = ITEMS.register( "tattered_helmet", TatteredArmorItem.Helmet::new );
	public static final RegistryObject< TatteredArmorItem > TATTERED_CHESTPLATE = ITEMS.register( "tattered_chestplate", TatteredArmorItem.Chestplate::new );
	public static final RegistryObject< TatteredArmorItem > TATTERED_LEGGINGS = ITEMS.register( "tattered_leggings", TatteredArmorItem.Leggings::new );
	public static final RegistryObject< TatteredArmorItem > TATTERED_BOOTS = ITEMS.register( "tattered_boots", TatteredArmorItem.Boots::new );
	public static final RegistryObject< CerberusFangItem > CERBERUS_FANG = ITEMS.register( "cerberus_fang", CerberusFangItem::new );
	public static final RegistryObject< EnderiumArmorItem > ENDERIUM_HELMET = ITEMS.register( "enderium_helmet", EnderiumArmorItem.Helmet::new );
	public static final RegistryObject< EnderiumArmorItem > ENDERIUM_CHESTPLATE = ITEMS.register( "enderium_chestplate", EnderiumArmorItem.Chestplate::new );
	public static final RegistryObject< EnderiumArmorItem > ENDERIUM_LEGGINGS = ITEMS.register( "enderium_leggings", EnderiumArmorItem.Leggings::new );
	public static final RegistryObject< EnderiumArmorItem > ENDERIUM_BOOTS = ITEMS.register( "enderium_boots", EnderiumArmorItem.Boots::new );
	public static final RegistryObject< EndShardItem > ENDERIUM_SHARD = ITEMS.register( "enderium_shard", EndShardItem::new );
	public static final RegistryObject< EnderiumIngotItem > ENDERIUM_INGOT = ITEMS.register( "enderium_ingot", EnderiumIngotItem::new );
	public static final RegistryObject< EnderiumSwordItem > ENDERIUM_SWORD = ITEMS.register( "enderium_sword", EnderiumSwordItem::new );
	public static final RegistryObject< EnderiumPickaxeItem > ENDERIUM_PICKAXE = ITEMS.register( "enderium_pickaxe", EnderiumPickaxeItem::new );
	public static final RegistryObject< EnderiumAxeItem > ENDERIUM_AXE = ITEMS.register( "enderium_axe", EnderiumAxeItem::new );
	public static final RegistryObject< EnderiumShovelItem > ENDERIUM_SHOVEL = ITEMS.register( "enderium_shovel", EnderiumShovelItem::new );
	public static final RegistryObject< EnderiumHoeItem > ENDERIUM_HOE = ITEMS.register( "enderium_hoe", EnderiumHoeItem::new );
	public static final RegistryObject< EndShardLocatorItem > ENDERIUM_SHARD_LOCATOR = ITEMS.register( "enderium_shard_locator", EndShardLocatorItem::new );
	public static final RegistryObject< EnderPouchItem > ENDER_POUCH = ITEMS.register( "ender_pouch", EnderPouchItem::new );
	public static final RegistryObject< WitherSwordItem > WITHER_SWORD = ITEMS.register( "wither_sword", WitherSwordItem::new );
	public static final RegistryObject< RecallPotionItem > RECALL_POTION = ITEMS.register( "recall_potion", RecallPotionItem::new );
	public static final RegistryObject< BadOmenPotionItem > BAD_OMEN_POTION = ITEMS.register( "bad_omen_potion", BadOmenPotionItem::new );
	public static final RegistryObject< SoulJarItem > SOUL_JAR = ITEMS.register( "soul_jar", SoulJarItem::new );

	// Potions
	public static final RegistryObject< Potion > WITHER_POTION = POTIONS.register( "wither", ()->new Potion( new MobEffectInstance( MobEffects.WITHER, Utility.secondsToTicks( 40.0 ) ) ) );
	public static final RegistryObject< Potion > WITHER_POTION_LONG = POTIONS.register( "long_wither", ()->new Potion( "wither", new MobEffectInstance( MobEffects.WITHER, Utility.secondsToTicks( 80.0 ) ) ) );
	public static final RegistryObject< Potion > WITHER_POTION_STRONG = POTIONS.register( "strong_wither", ()->new Potion( "wither", new MobEffectInstance( MobEffects.WITHER, Utility.secondsToTicks( 20.0 ), 1 ) ) );

	// Treasure Bags
	public static final RegistryObject< TreasureBagItem > UNDEAD_ARMY_TREASURE_BAG = ITEMS.register( "undead_army_treasure_bag", TreasureBagItem.UndeadArmy::new );
	public static final RegistryObject< TreasureBagItem > ELDER_GUARDIAN_TREASURE_BAG = ITEMS.register( "elder_guardian_treasure_bag", TreasureBagItem.ElderGuardian::new );
	public static final RegistryObject< TreasureBagItem > WITHER_TREASURE_BAG = ITEMS.register( "wither_treasure_bag", TreasureBagItem.Wither::new );
	public static final RegistryObject< TreasureBagItem > ENDER_DRAGON_TREASURE_BAG = ITEMS.register( "ender_dragon_treasure_bag", TreasureBagItem.EnderDragon::new );
	public static final RegistryObject< TreasureBagItem > FISHING_TREASURE_BAG = ITEMS.register( "fishing_treasure_bag", TreasureBagItem.Fishing::new );
	public static final RegistryObject< TreasureBagItem > PILLAGER_TREASURE_BAG = ITEMS.register( "pillager_treasure_bag", TreasureBagItem.Pillager::new );

	// Item Blocks
	public static final RegistryObject< EndShardOre.EndShardOreItem > ENDERIUM_SHARD_ORE_ITEM = ITEMS.register( "enderium_shard_ore", EndShardOre.EndShardOreItem::new );
	public static final RegistryObject< EnderiumBlock.EndBlockItem > ENDERIUM_BLOCK_ITEM = ITEMS.register( "enderium_block", EnderiumBlock.EndBlockItem::new );
	public static final RegistryObject< InfestedEndStone.InfestedEndStoneItem > INFESTED_END_STONE_ITEM = ITEMS.register( "infested_end_stone", InfestedEndStone.InfestedEndStoneItem::new );

	// Spawn Eggs
	public static final RegistryObject< SpawnEggItem > ILLUSIONER_SPAWN_EGG = ITEMS.register( "illusioner_spawn_egg", createEggSupplier( ()->EntityType.ILLUSIONER, 0x135a97, 9804699 ) );
	public static final RegistryObject< SpawnEggItem > CREEPERLING_SPAWN_EGG = ITEMS.register( "creeperling_spawn_egg", createEggSupplier( CREEPERLING, 0x0da70b, 0x000000 ) );
	public static final RegistryObject< SpawnEggItem > TANK_SPAWN_EGG = ITEMS.register( "tank_spawn_egg", createEggSupplier( TANK, 0xc1c1c1, 0x949494 ) );
	public static final RegistryObject< SpawnEggItem > CURSED_ARMOR_SPAWN_EGG = ITEMS.register( "cursed_armor_spawn_egg", createEggSupplier( CURSED_ARMOR, 0x808080, 0xe1e1e1 ) );
	public static final RegistryObject< SpawnEggItem > CERBERUS_SPAWN_EGG = ITEMS.register( "cerberus_spawn_egg", createEggSupplier( CERBERUS, 0x212121, 0xe0e0e0 ) );
	public static final RegistryObject< SpawnEggItem > BLACK_WIDOW_SPAWN_EGG = ITEMS.register( "black_widow_spawn_egg", createEggSupplier( BLACK_WIDOW, 0x212121, 0xe12121 ) );

	static Supplier< SpawnEggItem > createEggSupplier( Supplier< ? extends EntityType< ? extends Mob > > type,
		int backgroundColor, int highlightColor
	) {
		return ()->new ForgeSpawnEggItem( type, backgroundColor, highlightColor, new Item.Properties().tab( ITEM_GROUP ) );
	}

	// Fake items (just to display icons etc.)
	static {
		Stream.of( "normal", "expert", "master", "bleeding" )
			.forEach( name->ITEMS.register( "advancement_" + name, FakeItem::new ) );
	}

	// Blocks
	public static final RegistryObject< EndShardOre > ENDERIUM_SHARD_ORE = BLOCKS.register( "enderium_shard_ore", EndShardOre::new );
	public static final RegistryObject< EnderiumBlock > ENDERIUM_BLOCK = BLOCKS.register( "enderium_block", EnderiumBlock::new );
	public static final RegistryObject< InfestedEndStone > INFESTED_END_STONE = BLOCKS.register( "infested_end_stone", InfestedEndStone::new );

	// Effects
	public static final RegistryObject< BleedingEffect > BLEEDING = MOB_EFFECTS.register( "bleeding", BleedingEffect::new );
	public static final RegistryObject< BleedingImmunityEffect > BLEEDING_IMMUNITY = MOB_EFFECTS.register( "bleeding_immunity", BleedingImmunityEffect::new );

	// Damage Sources
	public static final DamageSource BLEEDING_SOURCE = new DamageSource( "bleeding" ).bypassArmor();

	// Particles
	public static final RegistryObject< SimpleParticleType > BLOOD = PARTICLE_TYPES.register( "blood_particle", ()->new SimpleParticleType( true ) );

	// Misc
	public static final CreativeModeTab ITEM_GROUP = CreativeModeTabHelper.newTab( "majruszsdifficulty.primary", BATTLE_STANDARD );

	// Triggers
	public static final GameStageTrigger GAME_STATE_TRIGGER = CriteriaTriggers.register( new GameStageTrigger() );
	public static final TreasureBagTrigger TREASURE_BAG_TRIGGER = CriteriaTriggers.register( new TreasureBagTrigger() );
	public static final BandageTrigger BANDAGE_TRIGGER = CriteriaTriggers.register( new BandageTrigger() );
	public static final BasicTrigger BASIC_TRIGGER = BasicTrigger.createRegisteredInstance( HELPER );

	// Sounds
	public static final RegistryObject< SoundEvent > UNDEAD_ARMY_APPROACHING = register( "undead_army.approaching" );
	public static final RegistryObject< SoundEvent > UNDEAD_ARMY_WAVE_STARTED = register( "undead_army.wave_started" );

	static RegistryObject< SoundEvent > register( String name ) {
		return SOUNDS_EVENTS.register( name, ()->new SoundEvent( getLocation( name ) ) );
	}

	// Loot Functions
	public static final RegistryObject< LootItemFunctionType > CURSE_RANDOMLY = LOOT_FUNCTIONS.register( "curse_randomly", CurseRandomlyFunction::newType );

	// Game Modifiers
	public static final AnnotationHandler ANNOTATION_HANDLER = new AnnotationHandler( MajruszsDifficulty.MOD_ID );

	public static UndeadArmyManager getUndeadArmyManager() {
		return GAME_DATA_SAVER.getUndeadArmyManager();
	}

	public static TreasureBagProgressManager getTreasureBagProgressManager() {
		return GAME_DATA_SAVER.getTreasureBagProgressManager();
	}

	public static ResourceLocation getLocation( String register ) {
		return HELPER.getLocation( register );
	}

	public static String getLocationString( String register ) {
		return HELPER.getLocationString( register );
	}

	public static ModelLayerLocation getModelLayer( String register, String layer ) {
		return HELPER.getModelLayer( register, layer );
	}

	public static ModelLayerLocation getModelLayer( String register ) {
		return HELPER.getModelLayer( register, "main" );
	}

	public static RenderType getEyesRenderType( String register ) {
		return HELPER.getEyesRenderType( register );
	}

	public static void initialize() {
		FMLJavaModLoadingContext loadingContext = FMLJavaModLoadingContext.get();
		final IEventBus modEventBus = loadingContext.getModEventBus();

		HELPER.registerAll();
		modEventBus.addListener( Registries::setup );
		modEventBus.addListener( Registries::setupClient );
		modEventBus.addListener( Registries::setupEntities );
		modEventBus.addListener( PacketHandler::registerPacket );

		IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
		forgeEventBus.addListener( Registries::onLoadingLevel );
		forgeEventBus.addListener( Registries::onSavingLevel );

		SERVER_CONFIG.register( ModLoadingContext.get() );
	}

	private static void setupClient( final FMLClientSetupEvent event ) {
		event.enqueueWork( RegistriesClient::setup ); // sets up client models etc.
	}

	public static void setupEntities( EntityAttributeCreationEvent event ) {
		event.put( CREEPERLING.get(), CreeperlingEntity.getAttributeMap() );
		event.put( TANK.get(), TankEntity.getAttributeMap() );
		event.put( BLACK_WIDOW.get(), BlackWidowEntity.getAttributeMap() );
		event.put( CURSED_ARMOR.get(), CursedArmorEntity.getAttributeMap() );
		event.put( CERBERUS.get(), CerberusEntity.getAttributeMap() );
	}

	private static void setup( final FMLCommonSetupEvent event ) {
		// FORGE: use SpawnPlacementRegisterEvent to register and modify spawn placements
		SpawnPlacements.register( CREEPERLING.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CreeperlingEntity::checkMobSpawnRules );
		SpawnPlacements.register( TANK.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TankEntity::checkMonsterSpawnRules );
		SpawnPlacements.register( BLACK_WIDOW.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BlackWidowEntity::checkMonsterSpawnRules );
		SpawnPlacements.register( CURSED_ARMOR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CursedArmorEntity::checkMonsterSpawnRules );
		SpawnPlacements.register( CERBERUS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CursedArmorEntity::checkMonsterSpawnRules );

		event.enqueueWork( ()->{
			addPotionRecipe( ()->Potions.WATER, CERBERUS_FANG, ()->Potions.MUNDANE );
			addPotionRecipe( ()->Potions.AWKWARD, CERBERUS_FANG, WITHER_POTION );
			addPotionRecipe( WITHER_POTION, ()->Items.REDSTONE, WITHER_POTION_LONG );
			addPotionRecipe( WITHER_POTION, ()->Items.GLOWSTONE_DUST, WITHER_POTION_STRONG );
		} );
	}

	private static void addPotionRecipe( Supplier< ? extends Potion > input, Supplier< ? extends Item > item, Supplier< ? extends Potion > output ) {
		BrewingRecipeRegistry.addRecipe( new IBrewingRecipe() {
			@Override
			public boolean isInput( ItemStack itemStack ) {
				return PotionUtils.getPotion( itemStack ).equals( input.get() );
			}

			@Override
			public boolean isIngredient( ItemStack itemStack ) {
				return itemStack.getItem().equals( item.get() );
			}

			@Override
			public ItemStack getOutput( ItemStack input, ItemStack ingredient ) {
				return this.isInput( input ) && this.isIngredient( ingredient ) ? PotionUtils.setPotion( new ItemStack( input.getItem() ), output.get() ) : ItemStack.EMPTY;
			}
		} );
	}

<<<<<<< HEAD
	public static void onLoadingLevel( WorldEvent.Load event ) {
		ServerLevel level = getOverworld( event.getWorld() );
		if( level == null )
=======
	public static void onLoadingLevel( LevelEvent.Load event ) {
		ServerLevel overworld = getOverworld( event.getLevel() );
		if( overworld == null )
>>>>>>> 255577cf (Made Undead Army Manager be part of GameDataSaver)
			return;

		GAME_DATA_SAVER = overworld.getDataStorage()
			.computeIfAbsent(
				nbt->new GameDataSaver( overworld, nbt ),
				()->new GameDataSaver( overworld ),
				MajruszsDifficulty.MOD_ID
			);

		TreasureBagManager.addTreasureBagTo( EntityType.ELDER_GUARDIAN, ELDER_GUARDIAN_TREASURE_BAG.get() );
		TreasureBagManager.addTreasureBagTo( EntityType.WITHER, WITHER_TREASURE_BAG.get() );
		TreasureBagManager.addTreasureBagTo( EntityType.ENDER_DRAGON, ENDER_DRAGON_TREASURE_BAG.get() );
	}

<<<<<<< HEAD
	public static void onSavingLevel( WorldEvent.Save event ) {
		ServerLevel level = getOverworld( event.getWorld() );
		if( level == null )
			return;

		GAME_DATA_SAVER.setDirty();
		UNDEAD_ARMY_MANAGER.setDirty();
=======
	public static void onSavingLevel( LevelEvent.Save event ) {
		ServerLevel overworld = getOverworld( event.getLevel() );
		if( overworld != null ) {
			GAME_DATA_SAVER.setDirty();
		}
>>>>>>> 255577cf (Made Undead Army Manager be part of GameDataSaver)
	}

	@Nullable
	private static ServerLevel getOverworld( LevelAccessor levelAccessor ) {
		ServerLevel overworld = levelAccessor.getServer() != null ? levelAccessor.getServer().getLevel( Level.OVERWORLD ) : null;
		return levelAccessor.equals( overworld ) ? overworld : null;
	}

	public static class Modifiers {
		public static final String DEFAULT = Registries.getLocationString( "default" );
		public static final String UNDEAD_ARMY = Registries.getLocationString( "undead_army" );
		public static final String GAME_STAGE = Registries.getLocationString( "game_stage" );
		public static final String TREASURE_BAG = Registries.getLocationString( "treasure_bag" );
		public static final String MOBS = Registries.getLocationString( "mobs" );
	}
}
