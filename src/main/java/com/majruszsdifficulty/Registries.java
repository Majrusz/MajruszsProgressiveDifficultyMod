package com.majruszsdifficulty;

import com.majruszsdifficulty.blocks.EndShardOre;
import com.majruszsdifficulty.blocks.EnderiumBlock;
import com.majruszsdifficulty.blocks.InfestedEndStone;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.majruszsdifficulty.effects.BleedingImmunityEffect;
import com.majruszsdifficulty.entities.BlackWidowEntity;
import com.majruszsdifficulty.entities.CreeperlingEntity;
import com.majruszsdifficulty.entities.CursedArmorEntity;
import com.majruszsdifficulty.entities.TankEntity;
import com.majruszsdifficulty.gamemodifiers.list.*;
import com.majruszsdifficulty.items.*;
import com.majruszsdifficulty.loot.CurseRandomlyFunction;
import com.majruszsdifficulty.treasurebags.LootProgressManager;
import com.majruszsdifficulty.treasurebags.TreasureBagManager;
import com.majruszsdifficulty.triggers.BandageTrigger;
import com.majruszsdifficulty.triggers.GameStageTrigger;
import com.majruszsdifficulty.triggers.TreasureBagTrigger;
import com.majruszsdifficulty.triggers.UndeadArmyDefeatedTrigger;
import com.majruszsdifficulty.undeadarmy.UndeadArmyConfig;
import com.majruszsdifficulty.undeadarmy.UndeadArmyEventsHandler;
import com.majruszsdifficulty.undeadarmy.UndeadArmyManager;
import com.mlib.annotations.AnnotationHandler;
import com.mlib.commands.Command;
import com.mlib.config.ConfigGroup;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.itemsets.ItemSet;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.majruszsdifficulty.MajruszsDifficulty.SERVER_CONFIG;

public class Registries {
	private static final RegistryHelper HELPER = new RegistryHelper( MajruszsDifficulty.MOD_ID );
	public static final ConfigGroup UNDEAD_ARMY_GROUP;
	public static final ConfigGroup MOBS_GROUP;

	static {
		GameModifier.addNewGroup( SERVER_CONFIG, Modifiers.DEFAULT );
		UNDEAD_ARMY_GROUP = GameModifier.addNewGroup( SERVER_CONFIG, Modifiers.UNDEAD_ARMY, "UndeadArmy", "" );
		GameModifier.addNewGroup( SERVER_CONFIG, Modifiers.GAME_STAGE, "GameStage", "" );
		GameModifier.addNewGroup( SERVER_CONFIG, Modifiers.TREASURE_BAG, "TreasureBag", "" );
		MOBS_GROUP = GameModifier.addNewGroup( SERVER_CONFIG, Modifiers.MOBS, "Mobs", "" );
	}

	// Groups
	static final DeferredRegister< Block > BLOCKS = HELPER.create( ForgeRegistries.Keys.BLOCKS );
	static final DeferredRegister< Item > ITEMS = HELPER.create( ForgeRegistries.Keys.ITEMS );
	static final DeferredRegister< EntityType< ? > > ENTITY_TYPES = HELPER.create( ForgeRegistries.Keys.ENTITY_TYPES );
	static final DeferredRegister< MobEffect > MOB_EFFECTS = HELPER.create( ForgeRegistries.Keys.MOB_EFFECTS );
	static final DeferredRegister< ParticleType< ? > > PARTICLE_TYPES = HELPER.create( ForgeRegistries.Keys.PARTICLE_TYPES );
	static final DeferredRegister< SoundEvent > SOUNDS_EVENTS = HELPER.create( ForgeRegistries.Keys.SOUND_EVENTS );
	static final DeferredRegister< LootItemFunctionType > LOOT_FUNCTIONS = HELPER.create( net.minecraft.core.registries.Registries.LOOT_FUNCTION_TYPE );

	// Entities
	public static final RegistryObject< EntityType< CreeperlingEntity > > CREEPERLING = ENTITY_TYPES.register( "creeperling", CreeperlingEntity.createSupplier() );
	public static final RegistryObject< EntityType< TankEntity > > TANK = ENTITY_TYPES.register( "tank", TankEntity.createSupplier() );
	public static final RegistryObject< EntityType< BlackWidowEntity > > BLACK_WIDOW = ENTITY_TYPES.register( "black_widow", BlackWidowEntity.createSupplier() );
	public static final RegistryObject< EntityType< CursedArmorEntity > > CURSED_ARMOR = ENTITY_TYPES.register( "cursed_armor", CursedArmorEntity.createSupplier() );

	// Items
	public static final RegistryObject< BandageItem > BANDAGE = ITEMS.register( "bandage", BandageItem::new );
	public static final RegistryObject< GoldenBandageItem > GOLDEN_BANDAGE = ITEMS.register( "golden_bandage", GoldenBandageItem::new );
	public static final RegistryObject< ClothItem > CLOTH = ITEMS.register( "cloth", ClothItem::new );
	public static final RegistryObject< UndeadBattleStandardItem > BATTLE_STANDARD = ITEMS.register( "undead_battle_standard", UndeadBattleStandardItem::new );
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
	public static final RegistryObject< WitherBowItem > WITHER_BOW = ITEMS.register( "wither_bow", WitherBowItem::new );
	public static final RegistryObject< RecallPotionItem > RECALL_POTION = ITEMS.register( "recall_potion", RecallPotionItem::new );
	public static final RegistryObject< BadOmenPotionItem > BAD_OMEN_POTION = ITEMS.register( "bad_omen_potion", BadOmenPotionItem::new );
	public static final RegistryObject< UndeadTotemItem > UNDEAD_TOTEM = ITEMS.register( "undead_totem", UndeadTotemItem::new );

	// Treasure Bags
	public static final RegistryObject< TreasureBagItem > UNDEAD_ARMY_TREASURE_BAG = ITEMS.register( "undead_army_treasure_bag", TreasureBagItem.UndeadArmy::new );
	public static final RegistryObject< TreasureBagItem > ELDER_GUARDIAN_TREASURE_BAG = ITEMS.register( "elder_guardian_treasure_bag", TreasureBagItem.ElderGuardian::new );
	public static final RegistryObject< TreasureBagItem > WITHER_TREASURE_BAG = ITEMS.register( "wither_treasure_bag", TreasureBagItem.Wither::new );
	public static final RegistryObject< TreasureBagItem > ENDER_DRAGON_TREASURE_BAG = ITEMS.register( "ender_dragon_treasure_bag", TreasureBagItem.EnderDragon::new );
	public static final RegistryObject< TreasureBagItem > FISHING_TREASURE_BAG = ITEMS.register( "fishing_treasure_bag", TreasureBagItem.Fishing::new );
	public static final RegistryObject< TreasureBagItem > PILLAGER_TREASURE_BAG = ITEMS.register( "pillager_treasure_bag", TreasureBagItem.Pillager::new );
	public static final RegistryObject< TreasureBagItem > WARDEN_TREASURE_BAG = ITEMS.register( "warden_treasure_bag", TreasureBagItem.Warden::new );

	// Item Blocks
	public static final RegistryObject< EndShardOre.EndShardOreItem > ENDERIUM_SHARD_ORE_ITEM = ITEMS.register( "enderium_shard_ore", EndShardOre.EndShardOreItem::new );
	public static final RegistryObject< EnderiumBlock.EndBlockItem > ENDERIUM_BLOCK_ITEM = ITEMS.register( "enderium_block", EnderiumBlock.EndBlockItem::new );
	public static final RegistryObject< InfestedEndStone.InfestedEndStoneItem > INFESTED_END_STONE_ITEM = ITEMS.register( "infested_end_stone", InfestedEndStone.InfestedEndStoneItem::new );

	// Spawn Eggs
	public static final RegistryObject< SpawnEggItem > ILLUSIONER_SPAWN_EGG = ITEMS.register( "illusioner_spawn_egg", createEggSupplier( ()->EntityType.ILLUSIONER, 0x135a97, 9804699 ) );
	public static final RegistryObject< SpawnEggItem > CREEPERLING_SPAWN_EGG = ITEMS.register( "creeperling_spawn_egg", createEggSupplier( CREEPERLING, 0x0da70b, 0x000000 ) );
	public static final RegistryObject< SpawnEggItem > TANK_SPAWN_EGG = ITEMS.register( "tank_spawn_egg", createEggSupplier( TANK, 0xc1c1c1, 0x949494 ) );
	public static final RegistryObject< SpawnEggItem > BLACK_WIDOW_SPAWN_EGG = ITEMS.register( "black_widow_spawn_egg", createEggSupplier( BLACK_WIDOW, 0x212121, 0xe12121 ) );
	public static final RegistryObject< SpawnEggItem > CURSED_ARMOR_SPAWN_EGG = ITEMS.register( "cursed_armor_spawn_egg", createEggSupplier( CURSED_ARMOR, 0x212121, 0xe12121 ) );

	static Supplier< SpawnEggItem > createEggSupplier( Supplier< ? extends EntityType< ? extends Mob > > type,
		int backgroundColor, int highlightColor
	) {
		return ()->new ForgeSpawnEggItem( type, backgroundColor, highlightColor, new Item.Properties() );
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
	static final List< Command > COMMANDS;
	public static UndeadArmyManager UNDEAD_ARMY_MANAGER;
	public static GameDataSaver GAME_DATA_SAVER;

	// Triggers
	public static final GameStageTrigger GAME_STATE_TRIGGER = CriteriaTriggers.register( new GameStageTrigger() );
	public static final TreasureBagTrigger TREASURE_BAG_TRIGGER = CriteriaTriggers.register( new TreasureBagTrigger() );
	public static final UndeadArmyDefeatedTrigger UNDEAD_ARMY_DEFEATED_TRIGGER = CriteriaTriggers.register( new UndeadArmyDefeatedTrigger() );
	public static final BandageTrigger BANDAGE_TRIGGER = CriteriaTriggers.register( new BandageTrigger() );
	public static final BasicTrigger BASIC_TRIGGER = BasicTrigger.createRegisteredInstance( HELPER );

	// Sounds
	public static final RegistryObject< SoundEvent > UNDEAD_ARMY_APPROACHING = register( "undead_army.approaching" );
	public static final RegistryObject< SoundEvent > UNDEAD_ARMY_WAVE_STARTED = register( "undead_army.wave_started" );

	static RegistryObject< SoundEvent > register( String name ) {
		return SOUNDS_EVENTS.register( name, ()->SoundEvent.createVariableRangeEvent( getLocation( name ) ) );
	}

	// Loot Functions
	public static final RegistryObject< LootItemFunctionType > CURSE_RANDOMLY = LOOT_FUNCTIONS.register( "curse_randomly", CurseRandomlyFunction::newType );

	// Game Modifiers
	public static final List< GameModifier > GAME_MODIFIERS;

	static {
		COMMANDS = new AnnotationHandler( "com.majruszsdifficulty.commands" ).getInstances( Command.class );
		GAME_MODIFIERS = new AnnotationHandler( "com.majruszsdifficulty" ).getInstances( GameModifier.class );
		new AnnotationHandler( "com.majruszsdifficulty.itemsets" ).getInstances( ItemSet.class );

		GAME_MODIFIERS.add( new BandageItem.BandageUse() );
		GAME_MODIFIERS.add( new BiteBleeding() );
		GAME_MODIFIERS.add( new BlockIllusionerFromJoiningRaids() );
		GAME_MODIFIERS.add( new CactusBleeding() );
		GAME_MODIFIERS.add( new CreeperChainReaction() );
		GAME_MODIFIERS.add( new CreeperExplodeBehindWall() );
		GAME_MODIFIERS.add( new CreeperExplosionImmunity() );
		GAME_MODIFIERS.add( new CreeperlingsCannotDestroyBlocks() );
		GAME_MODIFIERS.add( new CreeperSpawnCharged() );
		GAME_MODIFIERS.add( new CreeperSpawnDebuffed() );
		GAME_MODIFIERS.add( new CreeperSplitIntoCreeperlings() );
		GAME_MODIFIERS.add( new DoubleLoot() );
		GAME_MODIFIERS.add( new DrownDebuffs() );
		GAME_MODIFIERS.add( new DrownedLightningAttack() );
		GAME_MODIFIERS.add( new EndermanTeleportAttack() );
		GAME_MODIFIERS.add( new EvokerWithTotem() );
		GAME_MODIFIERS.add( new ExperienceBonus() );
		GAME_MODIFIERS.add( new FallDebuffs() );
		GAME_MODIFIERS.add( new IncreaseGameStage() );
		GAME_MODIFIERS.add( new JockeySpawn() );
		GAME_MODIFIERS.add( new LootProgressManager() );
		GAME_MODIFIERS.add( new MobsSpawnStronger() );
		GAME_MODIFIERS.add( new PhantomLevitationAttack() );
		GAME_MODIFIERS.add( new PiglinsInGroup() );
		GAME_MODIFIERS.add( new PowerfulExplosions() );
		GAME_MODIFIERS.add( new SharpToolsBleeding() );
		GAME_MODIFIERS.add( new ShulkerBlindnessAttack() );
		GAME_MODIFIERS.add( new SkeletonsInGroup() );
		GAME_MODIFIERS.add( new SlimeSlownessAttack() );
		GAME_MODIFIERS.add( new SpawnBlocker() );
		GAME_MODIFIERS.add( new SpawnPlayerZombie() );
		GAME_MODIFIERS.add( new SpiderPoisonAttack() );
		GAME_MODIFIERS.add( new ThrowableWeaponsBleeding() );
		GAME_MODIFIERS.add( new TreasureBagManager() );
		GAME_MODIFIERS.add( new UndeadArmyEventsHandler() );
		GAME_MODIFIERS.add( new ZombiesInGroup() );

		new UndeadArmyConfig(); // we need to make sure that this class is loaded before the configs are registered
	}

	public static ResourceLocation getLocation( String register ) {
		return HELPER.getLocation( register );
	}

	public static String getLocationString( String register ) {
		return getLocation( register ).toString();
	}

	public static ModelLayerLocation getModelLayer( String register, String layer ) {
		return new ModelLayerLocation( getLocation( register ), layer );
	}

	public static ModelLayerLocation getModelLayer( String register ) {
		return new ModelLayerLocation( getLocation( register ), "main" );
	}

	public static RenderType getEyesRenderType( String register ) {
		return RenderType.eyes( getLocation( register ) );
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
	}

	private static void setup( final FMLCommonSetupEvent event ) {
		SpawnPlacements.register( CREEPERLING.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CreeperlingEntity::checkMobSpawnRules );
		SpawnPlacements.register( TANK.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TankEntity::checkMonsterSpawnRules );
		SpawnPlacements.register( BLACK_WIDOW.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BlackWidowEntity::checkMonsterSpawnRules );
		SpawnPlacements.register( CURSED_ARMOR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CursedArmorEntity::checkMonsterSpawnRules );
	}

	public static void onLoadingLevel( LevelEvent.Load event ) {
		ServerLevel level = getOverworld( event.getLevel() );
		if( level == null )
			return;

		DimensionDataStorage manager = level.getDataStorage();
		UNDEAD_ARMY_MANAGER = manager.computeIfAbsent( nbt->UndeadArmyManager.load( nbt, level ), ()->new UndeadArmyManager( level ), UndeadArmyManager.DATA_NAME );
		GAME_DATA_SAVER = manager.computeIfAbsent( GameDataSaver::load, GameDataSaver::new, GameDataSaver.DATA_NAME );

		TreasureBagManager.addTreasureBagTo( EntityType.ELDER_GUARDIAN, ELDER_GUARDIAN_TREASURE_BAG.get() );
		TreasureBagManager.addTreasureBagTo( EntityType.WITHER, WITHER_TREASURE_BAG.get() );
		TreasureBagManager.addTreasureBagTo( EntityType.ENDER_DRAGON, ENDER_DRAGON_TREASURE_BAG.get() );
		TreasureBagManager.addTreasureBagTo( EntityType.WARDEN, WARDEN_TREASURE_BAG.get() );
	}

	public static void onSavingLevel( LevelEvent.Save event ) {
		ServerLevel level = getOverworld( event.getLevel() );
		if( level == null )
			return;

		GAME_DATA_SAVER.setDirty();
		UNDEAD_ARMY_MANAGER.setDirty();
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
