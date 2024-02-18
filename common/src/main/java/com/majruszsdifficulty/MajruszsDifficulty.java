package com.majruszsdifficulty;

import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszlibrary.emitter.ParticleEmitter;
import com.majruszlibrary.events.OnGameInitialized;
import com.majruszlibrary.item.ItemHelper;
import com.majruszlibrary.modhelper.ModHelper;
import com.majruszlibrary.network.NetworkObject;
import com.majruszlibrary.registry.Custom;
import com.majruszlibrary.registry.RegistryGroup;
import com.majruszlibrary.registry.RegistryObject;
import com.majruszlibrary.time.TimeHelper;
import com.majruszsdifficulty.blocks.*;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.data.WorldData;
import com.majruszsdifficulty.effects.Bleeding;
import com.majruszsdifficulty.effects.BleedingImmunity;
import com.majruszsdifficulty.effects.GlassRegeneration;
import com.majruszsdifficulty.effects.bleeding.BleedingParticles;
import com.majruszsdifficulty.entity.*;
import com.majruszsdifficulty.gamestage.GameStageAdvancement;
import com.majruszsdifficulty.items.*;
import com.majruszsdifficulty.loot.CurseRandomly;
import com.majruszsdifficulty.particles.BloodParticle;
import com.majruszsdifficulty.recipes.SoulJarShieldRecipe;
import com.majruszsdifficulty.treasurebag.TreasureBagHelper;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public class MajruszsDifficulty {
	public static final String MOD_ID = "majruszsdifficulty";
	public static final ModHelper HELPER = ModHelper.create( MOD_ID );

	// Data
	static {
		HELPER.config( Config.class ).autoSync().create();
	}

	public static final com.majruszlibrary.data.WorldData WORLD_DATA = HELPER.worldData( WorldData.class ).client( WorldData.Client.class ).create();

	// Registry Groups
	public static final RegistryGroup< Block > BLOCKS = HELPER.create( BuiltInRegistries.BLOCK );
	public static final RegistryGroup< CreativeModeTab > CREATIVE_MODE_TABS = HELPER.create( BuiltInRegistries.CREATIVE_MODE_TAB );
	public static final RegistryGroup< EntityType< ? > > ENTITY_TYPES = HELPER.create( BuiltInRegistries.ENTITY_TYPE );
	public static final RegistryGroup< Item > ITEMS = HELPER.create( BuiltInRegistries.ITEM );
	public static final RegistryGroup< LootItemFunctionType > LOOT_FUNCTIONS = HELPER.create( BuiltInRegistries.LOOT_FUNCTION_TYPE );
	public static final RegistryGroup< MobEffect > MOB_EFFECTS = HELPER.create( BuiltInRegistries.MOB_EFFECT );
	public static final RegistryGroup< ParticleType< ? > > PARTICLES = HELPER.create( BuiltInRegistries.PARTICLE_TYPE );
	public static final RegistryGroup< Potion > POTIONS = HELPER.create( BuiltInRegistries.POTION );
	public static final RegistryGroup< RecipeSerializer< ? > > RECIPES = HELPER.create( BuiltInRegistries.RECIPE_SERIALIZER );
	public static final RegistryGroup< SoundEvent > SOUND_EVENTS = HELPER.create( BuiltInRegistries.SOUND_EVENT );

	// Network
	public static final NetworkObject< BleedingParticles.Message > BLEEDING_GUI = HELPER.create( "bleeding_gui", BleedingParticles.Message.class );
	public static final NetworkObject< TreasureBag.RightClickAction > TREASURE_BAG_RIGHT_CLICK_NETWORK = HELPER.create( "treasure_bag_right_click", TreasureBag.RightClickAction.class );
	public static final NetworkObject< TreasureBagHelper.Progress > TREASURE_BAG_PROGRESS_NETWORK = HELPER.create( "treasure_bag_progress", TreasureBagHelper.Progress.class );

	// Entities
	public static final RegistryObject< EntityType< Cerberus > > CERBERUS_ENTITY = ENTITY_TYPES.create( "cerberus", Cerberus::createEntityType );
	public static final RegistryObject< EntityType< Creeperling > > CREEPERLING_ENTITY = ENTITY_TYPES.create( "creeperling", Creeperling::createEntityType );
	public static final RegistryObject< EntityType< CursedArmor > > CURSED_ARMOR_ENTITY = ENTITY_TYPES.create( "cursed_armor", CursedArmor::createEntityType );
	public static final RegistryObject< EntityType< Giant > > GIANT_ENTITY = ENTITY_TYPES.create( "giant", Giant::createEntityType );
	public static final RegistryObject< EntityType< Illusioner > > ILLUSIONER_ENTITY = ENTITY_TYPES.create( "illusioner", Illusioner::createEntityType );
	public static final RegistryObject< EntityType< Tank > > TANK_ENTITY = ENTITY_TYPES.create( "tank", Tank::createEntityType );

	// Effects
	public static final RegistryObject< Bleeding > BLEEDING_EFFECT = MOB_EFFECTS.create( "bleeding", Bleeding::new );
	public static final RegistryObject< BleedingImmunity > BLEEDING_IMMUNITY_EFFECT = MOB_EFFECTS.create( "bleeding_immunity", BleedingImmunity::new );
	public static final RegistryObject< GlassRegeneration > GLASS_REGENERATION_EFFECT = MOB_EFFECTS.create( "glass_regeneration", GlassRegeneration::new );

	// Blocks
	public static final RegistryObject< EnderiumBlock > ENDERIUM_BLOCK = BLOCKS.create( "enderium_block", EnderiumBlock::new );
	public static final RegistryObject< EnderiumShardOre > ENDERIUM_SHARD_ORE_BLOCK = BLOCKS.create( "enderium_shard_ore", EnderiumShardOre::new );
	public static final RegistryObject< FragileEndStone > FRAGILE_END_STONE_BLOCK = BLOCKS.create( "fragile_end_stone", FragileEndStone::new );
	public static final RegistryObject< InfernalSponge > INFERNAL_SPONGE_BLOCK = BLOCKS.create( "infernal_sponge", InfernalSponge::new );
	public static final RegistryObject< InfestedEndStone > INFESTED_END_STONE_BLOCK = BLOCKS.create( "infested_end_stone", InfestedEndStone::new );
	public static final RegistryObject< SoakedInfernalSponge > SOAKED_INFERNAL_SPONGE_BLOCK = BLOCKS.create( "soaked_infernal_sponge", SoakedInfernalSponge::new );

	// Items 
	public static final RegistryObject< Bandage > BANDAGE_ITEM = ITEMS.create( "bandage", Bandage.normal() );
	public static final RegistryObject< CerberusFang > CERBERUS_FANG_ITEM = ITEMS.create( "cerberus_fang", CerberusFang::new );
	public static final RegistryObject< Cloth > CLOTH_ITEM = ITEMS.create( "cloth", Cloth::new );
	public static final RegistryObject< EnderiumTool.Axe > ENDERIUM_AXE_ITEM = ITEMS.create( "enderium_axe", EnderiumTool.Axe::new );
	public static final RegistryObject< EnderiumArmor > ENDERIUM_BOOTS_ITEM = ITEMS.create( "enderium_boots", EnderiumArmor.boots() );
	public static final RegistryObject< EnderiumArmor > ENDERIUM_CHESTPLATE_ITEM = ITEMS.create( "enderium_chestplate", EnderiumArmor.chestplate() );
	public static final RegistryObject< EnderiumArmor > ENDERIUM_HELMET_ITEM = ITEMS.create( "enderium_helmet", EnderiumArmor.helmet() );
	public static final RegistryObject< EnderiumTool.Hoe > ENDERIUM_HOE_ITEM = ITEMS.create( "enderium_hoe", EnderiumTool.Hoe::new );
	public static final RegistryObject< EnderiumIngot > ENDERIUM_INGOT_ITEM = ITEMS.create( "enderium_ingot", EnderiumIngot::new );
	public static final RegistryObject< EnderiumArmor > ENDERIUM_LEGGINGS_ITEM = ITEMS.create( "enderium_leggings", EnderiumArmor.leggings() );
	public static final RegistryObject< EnderiumTool.Pickaxe > ENDERIUM_PICKAXE_ITEM = ITEMS.create( "enderium_pickaxe", EnderiumTool.Pickaxe::new );
	public static final RegistryObject< EnderiumShard > ENDERIUM_SHARD_ITEM = ITEMS.create( "enderium_shard", EnderiumShard::new );
	public static final RegistryObject< EnderiumShardLocator > ENDERIUM_SHARD_LOCATOR_ITEM = ITEMS.create( "enderium_shard_locator", EnderiumShardLocator::new );
	public static final RegistryObject< EnderiumTool.Shovel > ENDERIUM_SHOVEL_ITEM = ITEMS.create( "enderium_shovel", EnderiumTool.Shovel::new );
	public static final RegistryObject< EnderiumTool.Sword > ENDERIUM_SWORD_ITEM = ITEMS.create( "enderium_sword", EnderiumTool.Sword::new );
	public static final RegistryObject< EnderiumSmithingTemplate > ENDERIUM_SMITHING_TEMPLATE_ITEM = ITEMS.create( "enderium_upgrade_smithing_template", EnderiumSmithingTemplate::new );
	public static final RegistryObject< EnderPouch > ENDER_POUCH_ITEM = ITEMS.create( "ender_pouch", EnderPouch::new );
	public static final RegistryObject< EvokerFangScroll > EVOKER_FANG_SCROLL_ITEM = ITEMS.create( "evoker_fang_scroll", EvokerFangScroll::new );
	public static final RegistryObject< Bandage > GOLDEN_BANDAGE_ITEM = ITEMS.create( "golden_bandage", Bandage.golden() );
	public static final RegistryObject< RecallPotion > RECALL_POTION_ITEM = ITEMS.create( "recall_potion", RecallPotion::new );
	public static final RegistryObject< SonicBoomScroll > SONIC_BOOM_SCROLL_ITEM = ITEMS.create( "sonic_boom_scroll", SonicBoomScroll::new );
	public static final RegistryObject< SoulJar > SOUL_JAR_ITEM = ITEMS.create( "soul_jar", SoulJar::new );
	public static final RegistryObject< TatteredArmor > TATTERED_BOOTS_ITEM = ITEMS.create( "tattered_boots", TatteredArmor.boots() );
	public static final RegistryObject< TatteredArmor > TATTERED_CHESTPLATE_ITEM = ITEMS.create( "tattered_chestplate", TatteredArmor.chestplate() );
	public static final RegistryObject< TatteredArmor > TATTERED_HELMET_ITEM = ITEMS.create( "tattered_helmet", TatteredArmor.helmet() );
	public static final RegistryObject< TatteredArmor > TATTERED_LEGGINGS_ITEM = ITEMS.create( "tattered_leggings", TatteredArmor.leggings() );
	public static final RegistryObject< UndeadBattleStandard > UNDEAD_BATTLE_STANDARD_ITEM = ITEMS.create( "undead_battle_standard", UndeadBattleStandard::new );
	public static final RegistryObject< WitherSword > WITHER_SWORD_ITEM = ITEMS.create( "wither_sword", WitherSword::new );

	// Items (blocks)
	public static final RegistryObject< EnderiumBlock.Item > ENDERIUM_BLOCK_ITEM = ITEMS.create( "enderium_block", EnderiumBlock.Item::new );
	public static final RegistryObject< EnderiumShardOre.Item > ENDERIUM_SHARD_ORE_ITEM = ITEMS.create( "enderium_shard_ore", EnderiumShardOre.Item::new );
	public static final RegistryObject< FragileEndStone.Item > FRAGILE_END_STONE_ITEM = ITEMS.create( "fragile_end_stone", FragileEndStone.Item::new );
	public static final RegistryObject< InfernalSponge.Item > INFERNAL_SPONGE_ITEM = ITEMS.create( "infernal_sponge", InfernalSponge.Item::new );
	public static final RegistryObject< InfestedEndStone.Item > INFESTED_END_STONE_ITEM = ITEMS.create( "infested_end_stone", InfestedEndStone.Item::new );
	public static final RegistryObject< SoakedInfernalSponge.Item > SOAKED_INFERNAL_SPONGE_ITEM = ITEMS.create( "soaked_infernal_sponge", SoakedInfernalSponge.Item::new );

	// Items (spawn eggs)
	public static final RegistryObject< SpawnEggItem > CERBERUS_SPAWN_EGG_ITEM = ITEMS.create( "cerberus_spawn_egg", ItemHelper.createEgg( CERBERUS_ENTITY, 0x212121, 0xe0e0e0 ) );
	public static final RegistryObject< SpawnEggItem > CREEPERLING_SPAWN_EGG_ITEM = ITEMS.create( "creeperling_spawn_egg", ItemHelper.createEgg( CREEPERLING_ENTITY, 0x0da70b, 0x000000 ) );
	public static final RegistryObject< SpawnEggItem > CURSED_ARMOR_SPAWN_EGG_ITEM = ITEMS.create( "cursed_armor_spawn_egg", ItemHelper.createEgg( CURSED_ARMOR_ENTITY, 0x808080, 0xe1e1e1 ) );
	public static final RegistryObject< SpawnEggItem > GIANT_SPAWN_EGG_ITEM = ITEMS.create( "giant_spawn_egg", ItemHelper.createEgg( GIANT_ENTITY, 0x00afaf, 0x799c65 ) );
	public static final RegistryObject< SpawnEggItem > ILLUSIONER_SPAWN_EGG_ITEM = ITEMS.create( "illusioner_spawn_egg", ItemHelper.createEgg( ILLUSIONER_ENTITY, 0x3e293c, 0x959b9b ) );
	public static final RegistryObject< SpawnEggItem > TANK_SPAWN_EGG_ITEM = ITEMS.create( "tank_spawn_egg", ItemHelper.createEgg( TANK_ENTITY, 0xc1c1c1, 0x949494 ) );

	// Items (treasure bags)
	public static final RegistryObject< TreasureBag > ANGLER_TREASURE_BAG_ITEM = ITEMS.create( "angler_treasure_bag", TreasureBag.angler() );
	public static final RegistryObject< TreasureBag > ELDER_GUARDIAN_TREASURE_BAG_ITEM = ITEMS.create( "elder_guardian_treasure_bag", TreasureBag.elderGuardian() );
	public static final RegistryObject< TreasureBag > ENDER_DRAGON_TREASURE_BAG_ITEM = ITEMS.create( "ender_dragon_treasure_bag", TreasureBag.enderDragon() );
	public static final RegistryObject< TreasureBag > PILLAGER_TREASURE_BAG_ITEM = ITEMS.create( "pillager_treasure_bag", TreasureBag.pillager() );
	public static final RegistryObject< TreasureBag > UNDEAD_ARMY_TREASURE_BAG_ITEM = ITEMS.create( "undead_army_treasure_bag", TreasureBag.undeadArmy() );
	public static final RegistryObject< TreasureBag > WARDEN_TREASURE_BAG_ITEM = ITEMS.create( "warden_treasure_bag", TreasureBag.warden() );
	public static final RegistryObject< TreasureBag > WITHER_TREASURE_BAG_ITEM = ITEMS.create( "wither_treasure_bag", TreasureBag.wither() );

	// Items (fake)
	static {
		ITEMS.create( "advancement_bleeding", FakeItem::new );
		ITEMS.create( "advancement_normal", FakeItem::new );
		ITEMS.create( "advancement_expert", FakeItem::new );
		ITEMS.create( "advancement_master", FakeItem::new );
	}

	// Potions
	public static final RegistryObject< Potion > WITHER_POTION = POTIONS.create( "wither", ()->new Potion( new MobEffectInstance( MobEffects.WITHER, TimeHelper.toTicks( 40.0 ) ) ) );
	public static final RegistryObject< Potion > WITHER_LONG_POTION = POTIONS.create( "long_wither", ()->new Potion( "wither", new MobEffectInstance( MobEffects.WITHER, TimeHelper.toTicks( 80.0 ) ) ) );
	public static final RegistryObject< Potion > WITHER_STRONG_POTION = POTIONS.create( "strong_wither", ()->new Potion( "wither", new MobEffectInstance( MobEffects.WITHER, TimeHelper.toTicks( 20.0 ), 1 ) ) );

	// Particles
	public static final RegistryObject< SimpleParticleType > BLOOD_PARTICLE = PARTICLES.create( "blood", ()->new SimpleParticleType( true ) {} );

	// Loot Functions
	public static final RegistryObject< LootItemFunctionType > CURSE_RANDOMLY_LOOT_FUNCTION = LOOT_FUNCTIONS.create( "curse_randomly", CurseRandomly::create );

	// Creative Mode Tabs
	public static final RegistryObject< CreativeModeTab > PRIMARY_MODE_TAB = CREATIVE_MODE_TABS.create( "primary", com.majruszsdifficulty.items.CreativeModeTabs.primary() );

	// Placed Features
	public static final ResourceKey< PlacedFeature > ENDERIUM_ORE_PLACED_FEATURE = ResourceKey.create( Registries.PLACED_FEATURE, HELPER.getLocation( "enderium_ore" ) );
	public static final ResourceKey< PlacedFeature > ENDERIUM_ORE_LARGE_PLACED_FEATURE = ResourceKey.create( Registries.PLACED_FEATURE, HELPER.getLocation( "enderium_ore_large" ) );
	public static final ResourceKey< PlacedFeature > FRAGILE_END_STONE_PLACED_FEATURE = ResourceKey.create( Registries.PLACED_FEATURE, HELPER.getLocation( "fragile_end_stone" ) );
	public static final ResourceKey< PlacedFeature > FRAGILE_END_STONE_LARGE_PLACED_FEATURE = ResourceKey.create( Registries.PLACED_FEATURE, HELPER.getLocation( "fragile_end_stone_large" ) );
	public static final ResourceKey< PlacedFeature > INFESTED_END_STONE_PLACED_FEATURE = ResourceKey.create( Registries.PLACED_FEATURE, HELPER.getLocation( "infested_end_stone" ) );

	// Damage Sources
	public static final ResourceKey< DamageType > BLEEDING_DAMAGE_SOURCE = ResourceKey.create( Registries.DAMAGE_TYPE, HELPER.getLocation( "bleeding" ) );

	// Advancements
	public static final GameStageAdvancement GAME_STAGE_ADVANCEMENT = new GameStageAdvancement();

	// Sounds 
	public static final RegistryObject< SoundEvent > UNDEAD_ARMY_APPROACHING_SOUND = MajruszsDifficulty.register( "undead_army.approaching" );
	public static final RegistryObject< SoundEvent > UNDEAD_ARMY_WAVE_STARTED_SOUND = MajruszsDifficulty.register( "undead_army.wave_started" );

	// Recipes
	public static final RegistryObject< RecipeSerializer< ? > > SOUL_JAR_SHIELD_RECIPE = RECIPES.create( "soul_jar_shield", SoulJarShieldRecipe.create() );

	static {
		OnGameInitialized.listen( MajruszsDifficulty::addDefaultEmitters );

		HELPER.create( Custom.Advancements.class, advancements->{
			advancements.register( GAME_STAGE_ADVANCEMENT );
		} );

		HELPER.create( Custom.Attributes.class, attributes->{
			attributes.register( CERBERUS_ENTITY.get(), Cerberus.createAttributes() );
			attributes.register( CURSED_ARMOR_ENTITY.get(), CursedArmor.createAttributes() );
			attributes.register( CREEPERLING_ENTITY.get(), Creeperling.createChildAttributes() );
			attributes.register( GIANT_ENTITY.get(), Giant.createAttributes() );
			attributes.register( ILLUSIONER_ENTITY.get(), Illusioner.createAttributes().build() );
			attributes.register( TANK_ENTITY.get(), Tank.createAttributes() );
		} );

		HELPER.create( Custom.SpawnPlacements.class, spawnPlacements->{
			spawnPlacements.register( CERBERUS_ENTITY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Cerberus::checkMonsterSpawnRules );
			spawnPlacements.register( CURSED_ARMOR_ENTITY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CursedArmor::checkMonsterSpawnRules );
			spawnPlacements.register( CREEPERLING_ENTITY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Creeperling::checkMonsterSpawnRules );
			spawnPlacements.register( GIANT_ENTITY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Giant::checkMonsterSpawnRules );
			spawnPlacements.register( ILLUSIONER_ENTITY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Illusioner::checkMonsterSpawnRules );
			spawnPlacements.register( TANK_ENTITY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Tank::checkMonsterSpawnRules );
		} );
	}

	private static RegistryObject< SoundEvent > register( String name ) {
		return SOUND_EVENTS.create( name, ()->SoundEvent.createVariableRangeEvent( HELPER.getLocation( name ) ) );
	}

	private static void addDefaultEmitters( OnGameInitialized data ) {
		ParticleEmitter.setDefault( BLOOD_PARTICLE.get(), new ParticleEmitter.Properties( ParticleEmitter.offset( 0.5f ), ParticleEmitter.speed( 0.025f, 0.075f ) ) );
	}

	private MajruszsDifficulty() {}

	@OnlyIn( Dist.CLIENT )
	public static class Client {
		static {
			HELPER.create( Custom.ItemProperties.class, itemProperties->{
				itemProperties.register( MajruszsDifficulty.ENDERIUM_SHARD_LOCATOR_ITEM.get(), new ResourceLocation( "shard_distance" ), EnderiumShardLocator.Client::getShardDistance );
			} );

			HELPER.create( Custom.ModelLayers.class, modelLayers->{
				modelLayers.register( CerberusRenderer.LAYER, ()->CerberusModel.MODEL.get().toLayerDefinition() );
				modelLayers.register( CerberusRenderer.ARMOR_LAYER, ()->CerberusModel.MODEL.get().toLayerDefinition( new CubeDeformation( 0.5f ) ) );
				modelLayers.register( CursedArmorRenderer.LAYER, ()->CursedArmorModel.MODEL.get().toLayerDefinition() );
				modelLayers.register( CursedArmorRenderer.INNER_ARMOR_LAYER, ()->LayerDefinition.create( CursedArmorModel.createMesh( new CubeDeformation( 0.5f ), 0.0f ), 64, 32 ) );
				modelLayers.register( CursedArmorRenderer.OUTER_ARMOR_LAYER, ()->LayerDefinition.create( CursedArmorModel.createMesh( new CubeDeformation( 1.0f ), 0.0f ), 64, 32 ) );
				modelLayers.register( CreeperlingRenderer.LAYER, ()->CreeperlingModel.MODEL.get().toLayerDefinition() );
				modelLayers.register( GiantRenderer.LAYER, ()->LayerDefinition.create( GiantModel.createMesh( CubeDeformation.NONE, 0.0f ), 64, 64 ) );
				modelLayers.register( TankRenderer.LAYER, ()->TankModel.MODEL.get().toLayerDefinition() );
				modelLayers.register( TankRenderer.ARMOR_LAYER, ()->TankModel.MODEL.get().toLayerDefinition( new CubeDeformation( 0.5f ) ) );
			} );

			HELPER.create( Custom.Particles.class, particles->{
				particles.register( MajruszsDifficulty.BLOOD_PARTICLE.get(), BloodParticle.Factory::new );
			} );

			HELPER.create( Custom.PotionRecipe.class, recipes->{
				recipes.register( ()->net.minecraft.world.item.alchemy.Potions.WATER, MajruszsDifficulty.CERBERUS_FANG_ITEM, ()->net.minecraft.world.item.alchemy.Potions.MUNDANE );
				recipes.register( ()->net.minecraft.world.item.alchemy.Potions.AWKWARD, MajruszsDifficulty.CERBERUS_FANG_ITEM, MajruszsDifficulty.WITHER_POTION );
				recipes.register( MajruszsDifficulty.WITHER_POTION, ()->net.minecraft.world.item.Items.REDSTONE, MajruszsDifficulty.WITHER_LONG_POTION );
				recipes.register( MajruszsDifficulty.WITHER_POTION, ()->net.minecraft.world.item.Items.GLOWSTONE_DUST, MajruszsDifficulty.WITHER_STRONG_POTION );
			} );

			HELPER.create( Custom.Renderers.class, renderers->{
				renderers.register( CERBERUS_ENTITY.get(), CerberusRenderer::new );
				renderers.register( CURSED_ARMOR_ENTITY.get(), CursedArmorRenderer::new );
				renderers.register( CREEPERLING_ENTITY.get(), CreeperlingRenderer::new );
				renderers.register( GIANT_ENTITY.get(), GiantRenderer::new );
				renderers.register( ILLUSIONER_ENTITY.get(), IllusionerRenderer::new );
				renderers.register( TANK_ENTITY.get(), TankRenderer::new );
			} );
		}
	}
}
