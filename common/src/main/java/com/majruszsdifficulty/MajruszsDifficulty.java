package com.majruszsdifficulty;

import com.majruszsdifficulty.blocks.FragileEndStone;
import com.majruszsdifficulty.blocks.InfestedEndStone;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.data.WorldData;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.majruszsdifficulty.effects.BleedingImmunityEffect;
import com.majruszsdifficulty.effects.GlassRegenerationEffect;
import com.majruszsdifficulty.entity.*;
import com.majruszsdifficulty.gamestage.GameStageAdvancement;
import com.majruszsdifficulty.items.CreativeModeTabs;
import com.majruszsdifficulty.items.FakeItem;
import com.majruszsdifficulty.particles.BloodParticle;
import com.mlib.annotation.Dist;
import com.mlib.annotation.OnlyIn;
import com.mlib.contexts.OnGameInitialized;
import com.mlib.contexts.OnParticlesRegistered;
import com.mlib.emitter.ParticleEmitter;
import com.mlib.item.ItemHelper;
import com.mlib.modhelper.ModHelper;
import com.mlib.registry.Custom;
import com.mlib.registry.RegistryGroup;
import com.mlib.registry.RegistryObject;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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

public class MajruszsDifficulty {
	public static final String MOD_ID = "majruszsdifficulty";
	public static final ModHelper HELPER = ModHelper.create( MOD_ID );

	// Data
	public static final Config CONFIG = HELPER.config( Config::new ).autoSync().create();
	public static final WorldData WORLD_DATA = HELPER.saved( WorldData::new );

	// Registry Groups
	public static final RegistryGroup< Block > BLOCKS = HELPER.create( BuiltInRegistries.BLOCK );
	public static final RegistryGroup< CreativeModeTab > CREATIVE_MODE_TABS = HELPER.create( BuiltInRegistries.CREATIVE_MODE_TAB );
	public static final RegistryGroup< EntityType< ? > > ENTITY_TYPES = HELPER.create( BuiltInRegistries.ENTITY_TYPE );
	public static final RegistryGroup< Item > ITEMS = HELPER.create( BuiltInRegistries.ITEM );
	public static final RegistryGroup< MobEffect > MOB_EFFECTS = HELPER.create( BuiltInRegistries.MOB_EFFECT );
	public static final RegistryGroup< ParticleType< ? > > PARTICLES = HELPER.create( BuiltInRegistries.PARTICLE_TYPE );

	// Entities
	public static final RegistryObject< EntityType< CerberusEntity > > CERBERUS = ENTITY_TYPES.create( "cerberus", CerberusEntity::createEntityType );
	public static final RegistryObject< EntityType< CreeperlingEntity > > CREEPERLING = ENTITY_TYPES.create( "creeperling", CreeperlingEntity::createEntityType );
	public static final RegistryObject< EntityType< TankEntity > > TANK = ENTITY_TYPES.create( "tank", TankEntity::createEntityType );

	// Blocks
	public static final RegistryObject< FragileEndStone > FRAGILE_END_STONE = BLOCKS.create( "fragile_end_stone", FragileEndStone::new );
	public static final RegistryObject< InfestedEndStone > INFESTED_END_STONE = BLOCKS.create( "infested_end_stone", InfestedEndStone::new );

	// Items
	public static final RegistryObject< FragileEndStone.Item > FRAGILE_END_STONE_ITEM = ITEMS.create( "fragile_end_stone", FragileEndStone.Item::new );
	public static final RegistryObject< InfestedEndStone.Item > INFESTED_END_STONE_ITEM = ITEMS.create( "infested_end_stone", InfestedEndStone.Item::new );

	// Items (spawn eggs)
	public static final RegistryObject< SpawnEggItem > CERBERUS_SPAWN_EGG = ITEMS.create( "cerberus_spawn_egg", ItemHelper.createEgg( CERBERUS, 0x212121, 0xe0e0e0 ) );
	public static final RegistryObject< SpawnEggItem > CREEPERLING_SPAWN_EGG = ITEMS.create( "creeperling_spawn_egg", ItemHelper.createEgg( CREEPERLING, 0x0da70b, 0x000000 ) );
	public static final RegistryObject< SpawnEggItem > TANK_SPAWN_EGG = ITEMS.create( "tank_spawn_egg", ItemHelper.createEgg( TANK, 0xc1c1c1, 0x949494 ) );

	// Items (fake)
	static {
		ITEMS.create( "advancement_normal", FakeItem::new );
		ITEMS.create( "advancement_expert", FakeItem::new );
		ITEMS.create( "advancement_master", FakeItem::new );
	}

	// Effects
	public static final RegistryObject< BleedingEffect > BLEEDING = MOB_EFFECTS.create( "bleeding", BleedingEffect::new );
	public static final RegistryObject< BleedingImmunityEffect > BLEEDING_IMMUNITY = MOB_EFFECTS.create( "bleeding_immunity", BleedingImmunityEffect::new );
	public static final RegistryObject< GlassRegenerationEffect > GLASS_REGENERATION = MOB_EFFECTS.create( "glass_regeneration", GlassRegenerationEffect::new );

	// Particles
	public static final RegistryObject< SimpleParticleType > BLOOD_PARTICLE = PARTICLES.create( "blood", ()->new SimpleParticleType( true ) {} );

	// Creative Mode Tabs
	public static final RegistryObject< CreativeModeTab > CREATIVE_MODE_TAB = CREATIVE_MODE_TABS.create( "primary", CreativeModeTabs.primary() );

	// Placed Features
	public static final ResourceKey< PlacedFeature > FRAGILE_END_STONE_PLACED = ResourceKey.create( Registries.PLACED_FEATURE, HELPER.getLocation( "fragile_end_stone" ) );
	public static final ResourceKey< PlacedFeature > FRAGILE_END_STONE_LARGE_PLACED = ResourceKey.create( Registries.PLACED_FEATURE, HELPER.getLocation( "fragile_end_stone_large" ) );
	public static final ResourceKey< PlacedFeature > INFESTED_END_STONE_PLACED = ResourceKey.create( Registries.PLACED_FEATURE, HELPER.getLocation( "infested_end_stone" ) );

	// Damage Sources
	public static final ResourceKey< DamageType > BLEEDING_SOURCE = ResourceKey.create( Registries.DAMAGE_TYPE, HELPER.getLocation( "bleeding" ) );

	// Advancements
	public static final GameStageAdvancement GAME_STAGE_ADVANCEMENT = new GameStageAdvancement();

	static {
		OnGameInitialized.listen( MajruszsDifficulty::setDefaultEmitters );

		HELPER.create( Custom.Advancements.class, advancements->{
			advancements.register( GAME_STAGE_ADVANCEMENT );
		} );

		HELPER.create( Custom.Attributes.class, attributes->{
			attributes.register( CERBERUS.get(), CerberusEntity.createAttributes() );
			attributes.register( CREEPERLING.get(), CreeperlingEntity.createChildAttributes() );
			attributes.register( TANK.get(), TankEntity.createAttributes() );
		} );

		HELPER.create( Custom.SpawnPlacements.class, spawnPlacements->{
			spawnPlacements.register( CERBERUS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CerberusEntity::checkMonsterSpawnRules );
			spawnPlacements.register( CREEPERLING.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CreeperlingEntity::checkMonsterSpawnRules );
			spawnPlacements.register( TANK.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TankEntity::checkMonsterSpawnRules );
		} );
	}

	private static void setDefaultEmitters( OnGameInitialized data ) {
		ParticleEmitter.setDefault( BLOOD_PARTICLE.get(), new ParticleEmitter.Properties( ParticleEmitter.offset( 0.5f ), ParticleEmitter.speed( 0.025f, 0.075f ) ) );
	}

	private MajruszsDifficulty() {}

	@OnlyIn( Dist.CLIENT )
	public static class Client {
		static {
			OnParticlesRegistered.listen( data->data.register( BLOOD_PARTICLE.get(), BloodParticle.Factory::new ) );

			HELPER.create( Custom.ModelLayers.class, modelLayers->{
				modelLayers.register( CerberusRenderer.LAYER, ()->CerberusModel.MODEL.get().toLayerDefinition() );
				modelLayers.register( CreeperlingRenderer.LAYER, ()->CreeperlingModel.MODEL.get().toLayerDefinition() );
				modelLayers.register( TankRenderer.LAYER, ()->TankModel.MODEL.get().toLayerDefinition() );
			} );

			HELPER.create( Custom.Renderers.class, renderers->{
				renderers.register( CERBERUS.get(), CerberusRenderer::new );
				renderers.register( CREEPERLING.get(), CreeperlingRenderer::new );
				renderers.register( TANK.get(), TankRenderer::new );
			} );
		}
	}
}