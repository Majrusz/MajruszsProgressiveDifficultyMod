package com.majruszsdifficulty;

import com.majruszsdifficulty.accessories.FishingLuckBonus;
import com.majruszsdifficulty.blocks.EndShardOre;
import com.majruszsdifficulty.blocks.EnderiumBlock;
import com.majruszsdifficulty.blocks.InfestedEndStone;
import com.majruszsdifficulty.commands.CommandsHelper;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.majruszsdifficulty.effects.BleedingImmunityEffect;
import com.majruszsdifficulty.entities.CreeperlingEntity;
import com.majruszsdifficulty.entities.GiantEntity;
import com.majruszsdifficulty.entities.TankEntity;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.list.*;
import com.majruszsdifficulty.items.*;
import com.majruszsdifficulty.itemsets.EnderiumSet;
import com.majruszsdifficulty.itemsets.OceanSet;
import com.majruszsdifficulty.itemsets.UndeadSet;
import com.majruszsdifficulty.lootmodifiers.DoubleLoot;
import com.majruszsdifficulty.treasurebags.TreasureBagManager;
import com.majruszsdifficulty.triggers.BandageTrigger;
import com.majruszsdifficulty.triggers.GameStageTrigger;
import com.majruszsdifficulty.triggers.TreasureBagTrigger;
import com.majruszsdifficulty.triggers.UndeadArmyDefeatedTrigger;
import com.majruszsdifficulty.undeadarmy.UndeadArmyConfig;
import com.majruszsdifficulty.undeadarmy.UndeadArmyEventsHandler;
import com.majruszsdifficulty.undeadarmy.UndeadArmyManager;
import com.mlib.commands.IRegistrableCommand;
import com.mlib.registries.DeferredRegisterHelper;
import com.mlib.triggers.BasicTrigger;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.renderer.texture.TextureAtlas;
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
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerLifecycleEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Registries {
	private static final DeferredRegisterHelper HELPER = new DeferredRegisterHelper( MajruszsDifficulty.MOD_ID );
	public static final CreativeModeTab ITEM_GROUP = new CustomItemGroup( "majruszs_tab" );

	// Groups
	static final DeferredRegister< Block > BLOCKS = HELPER.create( ForgeRegistries.Keys.BLOCKS );
	static final DeferredRegister< Item > ITEMS = HELPER.create( ForgeRegistries.Keys.ITEMS );
	static final DeferredRegister< EntityType< ? > > ENTITY_TYPES = HELPER.create( ForgeRegistries.Keys.ENTITY_TYPES );
	static final DeferredRegister< MobEffect > MOB_EFFECTS = HELPER.create( ForgeRegistries.Keys.MOB_EFFECTS );
	static final DeferredRegister< ParticleType< ? > > PARTICLE_TYPES = HELPER.create( ForgeRegistries.Keys.PARTICLE_TYPES );
	static final DeferredRegister< SoundEvent > SOUNDS_EVENTS = HELPER.create( ForgeRegistries.Keys.SOUND_EVENTS );
	static final DeferredRegister< GlobalLootModifierSerializer< ? > > LOOT_MODIFIERS = HELPER.create( ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS );

	// Entities
	public static final RegistryObject< EntityType< GiantEntity > > GIANT = ENTITY_TYPES.register( "giant", GiantEntity.createSupplier() );
	public static final RegistryObject< EntityType< CreeperlingEntity > > CREEPERLING = ENTITY_TYPES.register( "creeperling", CreeperlingEntity.createSupplier() );
	public static final RegistryObject< EntityType< TankEntity > > TANK = ENTITY_TYPES.register( "tank", TankEntity.createSupplier() );

	// Items
	public static final RegistryObject< BandageItem > BANDAGE = ITEMS.register( "bandage", BandageItem::new );
	public static final RegistryObject< GoldenBandageItem > GOLDEN_BANDAGE = ITEMS.register( "golden_bandage", GoldenBandageItem::new );
	public static final RegistryObject< HermesBootsItem > HERMES_BOOTS = ITEMS.register( "hermes_boots", HermesBootsItem::new );
	public static final RegistryObject< ClothItem > CLOTH = ITEMS.register( "cloth", ClothItem::new );
	public static final RegistryObject< UndeadBattleStandardItem > BATTLE_STANDARD = ITEMS.register( "undead_battle_standard", UndeadBattleStandardItem::new );
	public static final RegistryObject< EnderiumArmorItem > ENDERIUM_HELMET = ITEMS.register( "enderium_helmet", EnderiumArmorItem.Helmet::new );
	public static final RegistryObject< EnderiumArmorItem > ENDERIUM_CHESTPLATE = ITEMS.register( "enderium_chestplate", EnderiumArmorItem.Chestplate::new );
	public static final RegistryObject< EnderiumArmorItem > ENDERIUM_LEGGINGS = ITEMS.register( "enderium_leggings", EnderiumArmorItem.Leggings::new );
	public static final RegistryObject< EnderiumArmorItem > ENDERIUM_BOOTS = ITEMS.register( "enderium_boots", EnderiumArmorItem.Boots::new );
	public static final RegistryObject< EndShardItem > ENDERIUM_SHARD = ITEMS.register( "enderium_shard", EndShardItem::new );
	public static final RegistryObject< EnderiumIngotItem > ENDERIUM_INGOT = ITEMS.register( "enderium_ingot", EnderiumIngotItem::new );
	public static final RegistryObject< EndShardLocatorItem > ENDERIUM_SHARD_LOCATOR = ITEMS.register( "enderium_shard_locator", EndShardLocatorItem::new );
	public static final RegistryObject< EnderiumSwordItem > ENDERIUM_SWORD = ITEMS.register( "enderium_sword", EnderiumSwordItem::new );
	public static final RegistryObject< EnderiumPickaxeItem > ENDERIUM_PICKAXE = ITEMS.register( "enderium_pickaxe", EnderiumPickaxeItem::new );
	public static final RegistryObject< EnderiumAxeItem > ENDERIUM_AXE = ITEMS.register( "enderium_axe", EnderiumAxeItem::new );
	public static final RegistryObject< EnderiumShovelItem > ENDERIUM_SHOVEL = ITEMS.register( "enderium_shovel", EnderiumShovelItem::new );
	public static final RegistryObject< EnderiumHoeItem > ENDERIUM_HOE = ITEMS.register( "enderium_hoe", EnderiumHoeItem::new );
	public static final RegistryObject< OceanShieldItem > OCEAN_SHIELD = ITEMS.register( "ocean_shield", OceanShieldItem::new );
	public static final RegistryObject< WitherSwordItem > WITHER_SWORD = ITEMS.register( "wither_sword", WitherSwordItem::new );
	public static final RegistryObject< AnglerEmblemItem > ANGLER_EMBLEM = ITEMS.register( "angler_emblem", AnglerEmblemItem::new );
	public static final RegistryObject< RecallPotionItem > RECALL_POTION = ITEMS.register( "recall_potion", RecallPotionItem::new );

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
	public static final RegistryObject< SpawnEggItem > GIANT_SPAWN_EGG = ITEMS.register( "giant_spawn_egg", createEggSupplier( ()->EntityType.GIANT, 44975, 7969893 ) );
	public static final RegistryObject< SpawnEggItem > ILLUSIONER_SPAWN_EGG = ITEMS.register( "illusioner_spawn_egg", createEggSupplier( ()->EntityType.ILLUSIONER, 0x135a97, 9804699 ) );
	public static final RegistryObject< SpawnEggItem > CREEPERLING_SPAWN_EGG = ITEMS.register( "creeperling_spawn_egg", createEggSupplier( CREEPERLING, 0x0da70b, 0x000000 ) );
	public static final RegistryObject< SpawnEggItem > TANK_SPAWN_EGG = ITEMS.register( "tank_spawn_egg", createEggSupplier( TANK, 0xc1c1c1, 0x949494 ) );

	static Supplier< SpawnEggItem > createEggSupplier( Supplier< ? extends EntityType< ? extends Mob > > type, int backgroundColor, int highlightColor ) {
		return ()->new ForgeSpawnEggItem( type, backgroundColor, highlightColor, new Item.Properties().tab( ITEM_GROUP ) );
	}

	// Fake items (just to display icons etc.)
	static {
		String[] fakeItemNames = new String[]{
			"normal", "expert", "master", "bleeding"
		};
		for( String name : fakeItemNames )
			ITEMS.register( "advancement_" + name, FakeItem::new );
	}

	// Item Sets
	public static final EnderiumSet ENDERIUM_SET = new EnderiumSet();
	public static final OceanSet OCEAN_SET = new OceanSet();
	public static final UndeadSet UNDEAD_SET = new UndeadSet();

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
	public static UndeadArmyManager UNDEAD_ARMY_MANAGER;
	public static GameDataSaver GAME_DATA_SAVER;

	// Triggers
	public static final GameStageTrigger GAME_STATE_TRIGGER = CriteriaTriggers.register( new GameStageTrigger() );
	public static final TreasureBagTrigger TREASURE_BAG_TRIGGER = CriteriaTriggers.register( new TreasureBagTrigger() );
	public static final UndeadArmyDefeatedTrigger UNDEAD_ARMY_DEFEATED_TRIGGER = CriteriaTriggers.register( new UndeadArmyDefeatedTrigger() );
	public static final BandageTrigger BANDAGE_TRIGGER = CriteriaTriggers.register( new BandageTrigger() );
	public static final BasicTrigger BASIC_TRIGGER = BasicTrigger.createRegisteredInstance( MajruszsDifficulty.MOD_ID );

	// Configs
	public static final SpawnDisabler.Config SPAWN_DISABLER_CONFIG = new SpawnDisabler.Config();

	// Sounds
	public static final RegistryObject< SoundEvent > UNDEAD_ARMY_APPROACHING;
	public static final RegistryObject< SoundEvent > UNDEAD_ARMY_WAVE_STARTED;

	static {
		UNDEAD_ARMY_APPROACHING = register( "undead_army.approaching" );
		UNDEAD_ARMY_WAVE_STARTED = register( "undead_army.wave_started" );
	}

	static RegistryObject< SoundEvent > register( String name ) {
		return SOUNDS_EVENTS.register( name, ()->new SoundEvent( getLocation( name ) ) );
	}

	// Loot Modifiers
	static {
		LOOT_MODIFIERS.register( "double_loot", DoubleLoot.Serializer::new );
	}

	// Game Modifiers
	public static final List< GameModifier > GAME_MODIFIERS = new ArrayList<>();

	static {
		GAME_MODIFIERS.add( new BandageItem.BandageUse() );
		GAME_MODIFIERS.add( new BiteBleeding() );
		GAME_MODIFIERS.add( new CactusBleeding() );
		GAME_MODIFIERS.add( new CreeperChainReaction() );
		GAME_MODIFIERS.add( new CreeperExplodeBehindWall() );
		GAME_MODIFIERS.add( new CreeperExplosionImmunity() );
		GAME_MODIFIERS.add( new CreeperlingsCannotDestroyBlocks() );
		GAME_MODIFIERS.add( new CreeperSpawnCharged() );
		GAME_MODIFIERS.add( new CreeperSpawnDebuffed() );
		GAME_MODIFIERS.add( new CreeperSplitIntoCreeperlings() );
		GAME_MODIFIERS.add( new DrownDebuffs() );
		GAME_MODIFIERS.add( new DrownedLightningAttack() );
		GAME_MODIFIERS.add( new EndermanTeleportAttack() );
		GAME_MODIFIERS.add( new EvokerWithTotem() );
		GAME_MODIFIERS.add( new ExperienceBonus() );
		GAME_MODIFIERS.add( new FallDebuffs() );
		GAME_MODIFIERS.add( new IncreaseGameStage() );
		GAME_MODIFIERS.add( new JockeySpawn() );
		GAME_MODIFIERS.add( new MobsSpawnStronger() );
		GAME_MODIFIERS.add( new PhantomLevitationAttack() );
		GAME_MODIFIERS.add( new PiglinsInGroup() );
		GAME_MODIFIERS.add( new PowerfulExplosions() );
		GAME_MODIFIERS.add( new SharpToolsBleeding() );
		GAME_MODIFIERS.add( new ShulkerBlindnessAttack() );
		GAME_MODIFIERS.add( new SkeletonsInGroup() );
		GAME_MODIFIERS.add( new SlimeSlownessAttack() );
		GAME_MODIFIERS.add( new SpawnPlayerZombie() );
		GAME_MODIFIERS.add( new SpiderPoisonAttack() );
		GAME_MODIFIERS.add( new ThrowableWeaponsBleeding() );
		GAME_MODIFIERS.add( new TreasureBagManager() );
		GAME_MODIFIERS.add( new UndeadArmyEventsHandler() );
		GAME_MODIFIERS.add( new WitherSkeletonWithSword() );
		GAME_MODIFIERS.add( new WitherSwordItem.Effect() );
		GAME_MODIFIERS.add( new ZombiesInGroup() );

		GAME_MODIFIERS.add( new FishingLuckBonus.Modifier() );

		new UndeadArmyConfig(); // we need to make sure that this class is loaded before the configs are registered
	}

	public static ResourceLocation getLocation( String register ) {
		return HELPER.getLocation( register );
	}

	public static String getLocationString( String register ) {
		return getLocation( register ).toString();
	}

	public static void initialize() {
		FMLJavaModLoadingContext loadingContext = FMLJavaModLoadingContext.get();
		final IEventBus modEventBus = loadingContext.getModEventBus();

		HELPER.registerAll();
		modEventBus.addListener( Registries::setup );
		modEventBus.addListener( Registries::setupClient );
		modEventBus.addListener( Registries::setupEntities );
		modEventBus.addListener( PacketHandler::registerPacket );
		DistExecutor.unsafeRunWhenOn( Dist.CLIENT, ()->()->modEventBus.addListener( Registries::onTextureStitch ) );

		IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
		forgeEventBus.addListener( Registries::onLoadingLevel );
		forgeEventBus.addListener( Registries::onSavingLevel );
		forgeEventBus.addListener( Registries::registerCommands );

		MajruszsDifficulty.CONFIG_HANDLER.register( ModLoadingContext.get() );
	}

	private static void setupClient( final FMLClientSetupEvent event ) {
		RegistriesClient.setup(); // sets up client models etc.
	}

	public static void setupEntities( EntityAttributeCreationEvent event ) {
		event.put( GIANT.get(), GiantEntity.getAttributeMap() );
		event.put( CREEPERLING.get(), CreeperlingEntity.getAttributeMap() );
		event.put( TANK.get(), TankEntity.getAttributeMap() );
	}

	private static void setup( final FMLCommonSetupEvent event ) {
		SpawnPlacements.register( GIANT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GiantEntity::checkMonsterSpawnRules );
		SpawnPlacements.register( CREEPERLING.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CreeperlingEntity::checkMobSpawnRules );
		SpawnPlacements.register( TANK.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TankEntity::checkMonsterSpawnRules );
	}

	private static void registerCommands( RegisterCommandsEvent event ) {
		for( IRegistrableCommand command : CommandsHelper.COMMANDS )
			command.register( event.getDispatcher() );
	}

	public static void onLoadingLevel( WorldEvent.Load event ) {
		ServerLevel level = getOverworld( event.getWorld() );
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

	public static void onSavingLevel( WorldEvent.Save event ) {
		ServerLevel level = getOverworld( event.getWorld() );
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

	@OnlyIn( Dist.CLIENT )
	private static void onTextureStitch( TextureStitchEvent.Pre event ) {
		final TextureAtlas map = event.getAtlas();
		if( InventoryMenu.BLOCK_ATLAS.equals( map.location() ) )
			event.addSprite( RegistriesClient.OCEAN_SHIELD_MATERIAL.texture() );
	}
}
