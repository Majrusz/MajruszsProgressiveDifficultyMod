package com.majruszsdifficulty;

import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszlibrary.emitter.ParticleEmitter;
import com.majruszlibrary.events.OnGameInitialized;
import com.majruszlibrary.item.ItemHelper;
import com.majruszlibrary.modhelper.ModHelper;
import com.majruszlibrary.registry.Custom;
import com.majruszlibrary.registry.RegistryGroup;
import com.majruszlibrary.registry.RegistryObject;
import com.majruszsdifficulty.blocks.*;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.data.WorldData;
import com.majruszsdifficulty.effects.Bleeding;
import com.majruszsdifficulty.effects.BleedingImmunity;
import com.majruszsdifficulty.effects.GlassRegeneration;
import com.majruszsdifficulty.entity.*;
import com.majruszsdifficulty.gamestage.GameStageAdvancement;
import com.majruszsdifficulty.items.*;
import com.majruszsdifficulty.loot.CurseRandomly;
import com.majruszsdifficulty.particles.BloodParticle;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
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

	public static final com.majruszlibrary.data.WorldData WORLD_DATA = HELPER.worldData( WorldData.class )
		.client( WorldData.Client.class )
		.setupDefaultValues( WorldData::setupDefaultValues )
		.create();

	// Registry Groups
	public static final RegistryGroup< Block > BLOCKS = HELPER.create( BuiltInRegistries.BLOCK );
	public static final RegistryGroup< CreativeModeTab > CREATIVE_MODE_TABS = HELPER.create( BuiltInRegistries.CREATIVE_MODE_TAB );
	public static final RegistryGroup< EntityType< ? > > ENTITY_TYPES = HELPER.create( BuiltInRegistries.ENTITY_TYPE );
	public static final RegistryGroup< Item > ITEMS = HELPER.create( BuiltInRegistries.ITEM );
	public static final RegistryGroup< LootItemFunctionType > LOOT_FUNCTIONS = HELPER.create( BuiltInRegistries.LOOT_FUNCTION_TYPE );
	public static final RegistryGroup< MobEffect > MOB_EFFECTS = HELPER.create( BuiltInRegistries.MOB_EFFECT );
	public static final RegistryGroup< ParticleType< ? > > PARTICLES = HELPER.create( BuiltInRegistries.PARTICLE_TYPE );
	public static final RegistryGroup< SoundEvent > SOUND_EVENTS = HELPER.create( BuiltInRegistries.SOUND_EVENT );

	public static class Entities {
		public static final RegistryObject< EntityType< Cerberus > > CERBERUS = ENTITY_TYPES.create( "cerberus", Cerberus::createEntityType );
		public static final RegistryObject< EntityType< Creeperling > > CREEPERLING = ENTITY_TYPES.create( "creeperling", Creeperling::createEntityType );
		public static final RegistryObject< EntityType< CursedArmor > > CURSED_ARMOR = ENTITY_TYPES.create( "cursed_armor", CursedArmor::createEntityType );
		public static final RegistryObject< EntityType< Giant > > GIANT = ENTITY_TYPES.create( "giant", Giant::createEntityType );
		public static final RegistryObject< EntityType< Tank > > TANK = ENTITY_TYPES.create( "tank", Tank::createEntityType );
	}

	public static class Effects {
		public static final RegistryObject< Bleeding > BLEEDING = MOB_EFFECTS.create( "bleeding", Bleeding::new );
		public static final RegistryObject< BleedingImmunity > BLEEDING_IMMUNITY = MOB_EFFECTS.create( "bleeding_immunity", BleedingImmunity::new );
		public static final RegistryObject< GlassRegeneration > GLASS_REGENERATION = MOB_EFFECTS.create( "glass_regeneration", GlassRegeneration::new );
	}

	public static class Blocks {
		public static final RegistryObject< EnderiumBlock > ENDERIUM_BLOCK = BLOCKS.create( "enderium_block", EnderiumBlock::new );
		public static final RegistryObject< EnderiumShardOre > ENDERIUM_SHARD_ORE = BLOCKS.create( "enderium_shard_ore", EnderiumShardOre::new );
		public static final RegistryObject< FragileEndStone > FRAGILE_END_STONE = BLOCKS.create( "fragile_end_stone", FragileEndStone::new );
		public static final RegistryObject< InfernalSponge > INFERNAL_SPONGE = BLOCKS.create( "infernal_sponge", InfernalSponge::new );
		public static final RegistryObject< InfestedEndStone > INFESTED_END_STONE = BLOCKS.create( "infested_end_stone", InfestedEndStone::new );
		public static final RegistryObject< SoakedInfernalSponge > SOAKED_INFERNAL_SPONGE = BLOCKS.create( "soaked_infernal_sponge", SoakedInfernalSponge::new );
	}

	public static class Items {
		public static final RegistryObject< Bandage > BANDAGE = ITEMS.create( "bandage", Bandage.normal() );
		public static final RegistryObject< CerberusFang > CERBERUS_FANG = ITEMS.create( "cerberus_fang", CerberusFang::new );
		public static final RegistryObject< Cloth > CLOTH = ITEMS.create( "cloth", Cloth::new );
		public static final RegistryObject< EnderiumTool.Axe > ENDERIUM_AXE = ITEMS.create( "enderium_axe", EnderiumTool.Axe::new );
		public static final RegistryObject< EnderiumArmor > ENDERIUM_BOOTS = ITEMS.create( "enderium_boots", EnderiumArmor.boots() );
		public static final RegistryObject< EnderiumArmor > ENDERIUM_CHESTPLATE = ITEMS.create( "enderium_chestplate", EnderiumArmor.chestplate() );
		public static final RegistryObject< EnderiumArmor > ENDERIUM_HELMET = ITEMS.create( "enderium_helmet", EnderiumArmor.helmet() );
		public static final RegistryObject< EnderiumTool.Hoe > ENDERIUM_HOE = ITEMS.create( "enderium_hoe", EnderiumTool.Hoe::new );
		public static final RegistryObject< EnderiumIngot > ENDERIUM_INGOT = ITEMS.create( "enderium_ingot", EnderiumIngot::new );
		public static final RegistryObject< EnderiumArmor > ENDERIUM_LEGGINGS = ITEMS.create( "enderium_leggings", EnderiumArmor.leggings() );
		public static final RegistryObject< EnderiumTool.Pickaxe > ENDERIUM_PICKAXE = ITEMS.create( "enderium_pickaxe", EnderiumTool.Pickaxe::new );
		public static final RegistryObject< EnderiumShard > ENDERIUM_SHARD = ITEMS.create( "enderium_shard", EnderiumShard::new );
		public static final RegistryObject< EnderiumTool.Shovel > ENDERIUM_SHOVEL = ITEMS.create( "enderium_shovel", EnderiumTool.Shovel::new );
		public static final RegistryObject< EnderiumTool.Sword > ENDERIUM_SWORD = ITEMS.create( "enderium_sword", EnderiumTool.Sword::new );
		public static final RegistryObject< EnderiumSmithingTemplate > ENDERIUM_SMITHING_TEMPLATE = ITEMS.create( "enderium_upgrade_smithing_template", EnderiumSmithingTemplate::new );
		public static final RegistryObject< EnderPouch > ENDER_POUCH = ITEMS.create( "ender_pouch", EnderPouch::new );
		public static final RegistryObject< EvokerFangScroll > EVOKER_FANG_SCROLL = ITEMS.create( "evoker_fang_scroll", EvokerFangScroll::new );
		public static final RegistryObject< Bandage > GOLDEN_BANDAGE = ITEMS.create( "golden_bandage", Bandage.golden() );
		public static final RegistryObject< RecallPotion > RECALL_POTION = ITEMS.create( "recall_potion", RecallPotion::new );
		public static final RegistryObject< SonicBoomScroll > SONIC_BOOM_SCROLL = ITEMS.create( "sonic_boom_scroll", SonicBoomScroll::new );
		public static final RegistryObject< TatteredArmor > TATTERED_BOOTS = ITEMS.create( "tattered_boots", TatteredArmor.boots() );
		public static final RegistryObject< TatteredArmor > TATTERED_CHESTPLATE = ITEMS.create( "tattered_chestplate", TatteredArmor.chestplate() );
		public static final RegistryObject< TatteredArmor > TATTERED_HELMET = ITEMS.create( "tattered_helmet", TatteredArmor.helmet() );
		public static final RegistryObject< TatteredArmor > TATTERED_LEGGINGS = ITEMS.create( "tattered_leggings", TatteredArmor.leggings() );
		public static final RegistryObject< UndeadBattleStandard > UNDEAD_BATTLE_STANDARD = ITEMS.create( "undead_battle_standard", UndeadBattleStandard::new );

		// Items (blocks)
		public static final RegistryObject< EnderiumBlock.Item > ENDERIUM_BLOCK = ITEMS.create( "enderium_block", EnderiumBlock.Item::new );
		public static final RegistryObject< EnderiumShardOre.Item > ENDERIUM_SHARD_ORE = ITEMS.create( "enderium_shard_ore", EnderiumShardOre.Item::new );
		public static final RegistryObject< FragileEndStone.Item > FRAGILE_END_STONE = ITEMS.create( "fragile_end_stone", FragileEndStone.Item::new );
		public static final RegistryObject< InfernalSponge.Item > INFERNAL_SPONGE = ITEMS.create( "infernal_sponge", InfernalSponge.Item::new );
		public static final RegistryObject< InfestedEndStone.Item > INFESTED_END_STONE = ITEMS.create( "infested_end_stone", InfestedEndStone.Item::new );
		public static final RegistryObject< SoakedInfernalSponge.Item > SOAKED_INFERNAL_SPONGE = ITEMS.create( "soaked_infernal_sponge", SoakedInfernalSponge.Item::new );

		// Items (spawn eggs)
		public static final RegistryObject< SpawnEggItem > CERBERUS_SPAWN_EGG = ITEMS.create( "cerberus_spawn_egg", ItemHelper.createEgg( Entities.CERBERUS, 0x212121, 0xe0e0e0 ) );
		public static final RegistryObject< SpawnEggItem > CREEPERLING_SPAWN_EGG = ITEMS.create( "creeperling_spawn_egg", ItemHelper.createEgg( Entities.CREEPERLING, 0x0da70b, 0x000000 ) );
		public static final RegistryObject< SpawnEggItem > CURSED_ARMOR_SPAWN_EGG = ITEMS.create( "cursed_armor_spawn_egg", ItemHelper.createEgg( Entities.CURSED_ARMOR, 0x808080, 0xe1e1e1 ) );
		public static final RegistryObject< SpawnEggItem > GIANT_SPAWN_EGG = ITEMS.create( "giant_spawn_egg", ItemHelper.createEgg( Entities.GIANT, 0x00afaf, 0x799c65 ) );
		public static final RegistryObject< SpawnEggItem > TANK_SPAWN_EGG = ITEMS.create( "tank_spawn_egg", ItemHelper.createEgg( Entities.TANK, 0xc1c1c1, 0x949494 ) );

		// Items (fake)
		static {
			ITEMS.create( "advancement_normal", FakeItem::new );
			ITEMS.create( "advancement_expert", FakeItem::new );
			ITEMS.create( "advancement_master", FakeItem::new );
		}
	}

	public static class Particles {
		public static final RegistryObject< SimpleParticleType > BLOOD = PARTICLES.create( "blood", ()->new SimpleParticleType( true ) {} );
	}

	public static class LootFunctions {
		public static final RegistryObject< LootItemFunctionType > CURSE_RANDOMLY = LOOT_FUNCTIONS.create( "curse_randomly", CurseRandomly::create );
	}

	public static class CreativeModeTabs {
		public static final RegistryObject< CreativeModeTab > PRIMARY = CREATIVE_MODE_TABS.create( "primary", com.majruszsdifficulty.items.CreativeModeTabs.primary() );
	}

	public static class PlacedFeatures {
		public static final ResourceKey< PlacedFeature > ENDERIUM_ORE = ResourceKey.create( Registries.PLACED_FEATURE, HELPER.getLocation( "enderium_ore" ) );
		public static final ResourceKey< PlacedFeature > ENDERIUM_ORE_LARGE = ResourceKey.create( Registries.PLACED_FEATURE, HELPER.getLocation( "enderium_ore_large" ) );
		public static final ResourceKey< PlacedFeature > FRAGILE_END_STONE = ResourceKey.create( Registries.PLACED_FEATURE, HELPER.getLocation( "fragile_end_stone" ) );
		public static final ResourceKey< PlacedFeature > FRAGILE_END_STONE_LARGE = ResourceKey.create( Registries.PLACED_FEATURE, HELPER.getLocation( "fragile_end_stone_large" ) );
		public static final ResourceKey< PlacedFeature > INFESTED_END_STONE = ResourceKey.create( Registries.PLACED_FEATURE, HELPER.getLocation( "infested_end_stone" ) );
	}

	public static class DamageSources {
		public static final ResourceKey< DamageType > BLEEDING = ResourceKey.create( Registries.DAMAGE_TYPE, HELPER.getLocation( "bleeding" ) );
	}

	public static class Advancements {
		public static final GameStageAdvancement GAME_STAGE = new GameStageAdvancement();
	}

	public static class Sounds {
		public static final RegistryObject< SoundEvent > UNDEAD_ARMY_APPROACHING = Sounds.register( "undead_army.approaching" );
		public static final RegistryObject< SoundEvent > UNDEAD_ARMY_WAVE_STARTED = Sounds.register( "undead_army.wave_started" );

		private static RegistryObject< SoundEvent > register( String name ) {
			return SOUND_EVENTS.create( name, ()->SoundEvent.createVariableRangeEvent( HELPER.getLocation( name ) ) );
		}
	}

	static {
		OnGameInitialized.listen( MajruszsDifficulty::addDefaultEmitters );

		HELPER.create( Custom.Advancements.class, advancements->{
			advancements.register( Advancements.GAME_STAGE );
		} );

		HELPER.create( Custom.Attributes.class, attributes->{
			attributes.register( Entities.CERBERUS.get(), Cerberus.createAttributes() );
			attributes.register( Entities.CURSED_ARMOR.get(), CursedArmor.createAttributes() );
			attributes.register( Entities.CREEPERLING.get(), Creeperling.createChildAttributes() );
			attributes.register( Entities.GIANT.get(), Giant.createAttributes() );
			attributes.register( Entities.TANK.get(), Tank.createAttributes() );
		} );

		HELPER.create( Custom.SpawnPlacements.class, spawnPlacements->{
			spawnPlacements.register( Entities.CERBERUS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Cerberus::checkMonsterSpawnRules );
			spawnPlacements.register( Entities.CURSED_ARMOR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CursedArmor::checkMonsterSpawnRules );
			spawnPlacements.register( Entities.CREEPERLING.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Creeperling::checkMonsterSpawnRules );
			spawnPlacements.register( Entities.GIANT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Giant::checkMonsterSpawnRules );
			spawnPlacements.register( Entities.TANK.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Tank::checkMonsterSpawnRules );
		} );
	}

	private static void addDefaultEmitters( OnGameInitialized data ) {
		ParticleEmitter.setDefault( Particles.BLOOD.get(), new ParticleEmitter.Properties( ParticleEmitter.offset( 0.5f ), ParticleEmitter.speed( 0.025f, 0.075f ) ) );
	}

	@OnlyIn( Dist.CLIENT )
	public static class Client {
		static {
			HELPER.create( Custom.ModelLayers.class, modelLayers->{
				modelLayers.register( CerberusRenderer.LAYER, ()->CerberusModel.MODEL.get().toLayerDefinition() );
				modelLayers.register( CursedArmorRenderer.LAYER, ()->CursedArmorModel.MODEL.get().toLayerDefinition() );
				modelLayers.register( CursedArmorRenderer.INNER_ARMOR_LAYER, ()->LayerDefinition.create( CursedArmorModel.createMesh( new CubeDeformation( 0.5f ), 0.0f ), 64, 32 ) );
				modelLayers.register( CursedArmorRenderer.OUTER_ARMOR_LAYER, ()->LayerDefinition.create( CursedArmorModel.createMesh( new CubeDeformation( 1.0f ), 0.0f ), 64, 32 ) );
				modelLayers.register( CreeperlingRenderer.LAYER, ()->CreeperlingModel.MODEL.get().toLayerDefinition() );
				modelLayers.register( GiantRenderer.LAYER, ()->LayerDefinition.create( GiantModel.createMesh( CubeDeformation.NONE, 0.0f ), 64, 64 ) );
				modelLayers.register( TankRenderer.LAYER, ()->TankModel.MODEL.get().toLayerDefinition() );
			} );

			HELPER.create( Custom.Particles.class, particles->{
				particles.register( Particles.BLOOD.get(), BloodParticle.Factory::new );
			} );

			HELPER.create( Custom.Renderers.class, renderers->{
				renderers.register( Entities.CERBERUS.get(), CerberusRenderer::new );
				renderers.register( Entities.CURSED_ARMOR.get(), CursedArmorRenderer::new );
				renderers.register( Entities.CREEPERLING.get(), CreeperlingRenderer::new );
				renderers.register( Entities.GIANT.get(), GiantRenderer::new );
				renderers.register( Entities.TANK.get(), TankRenderer::new );
			} );
		}
	}
}
