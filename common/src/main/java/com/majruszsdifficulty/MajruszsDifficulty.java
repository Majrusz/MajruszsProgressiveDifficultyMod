package com.majruszsdifficulty;

import com.majruszsdifficulty.blocks.FragileEndStone;
import com.majruszsdifficulty.blocks.InfestedEndStone;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.data.WorldData;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.majruszsdifficulty.effects.BleedingImmunityEffect;
import com.majruszsdifficulty.gamestage.GameStageAdvancement;
import com.majruszsdifficulty.items.CreativeModeTabs;
import com.majruszsdifficulty.items.FakeItem;
import com.majruszsdifficulty.particles.BloodParticle;
import com.mlib.annotation.Dist;
import com.mlib.annotation.OnlyIn;
import com.mlib.contexts.OnGameInitialized;
import com.mlib.contexts.OnParticlesRegistered;
import com.mlib.emitter.ParticleEmitter;
import com.mlib.modhelper.ModHelper;
import com.mlib.registry.RegistryGroup;
import com.mlib.registry.RegistryObject;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
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
	public static final RegistryGroup< Item > ITEMS = HELPER.create( BuiltInRegistries.ITEM );
	public static final RegistryGroup< MobEffect > MOB_EFFECTS = HELPER.create( BuiltInRegistries.MOB_EFFECT );
	public static final RegistryGroup< ParticleType< ? > > PARTICLES = HELPER.create( BuiltInRegistries.PARTICLE_TYPE );

	// Blocks
	public static final RegistryObject< FragileEndStone > FRAGILE_END_STONE = BLOCKS.create( "fragile_end_stone", FragileEndStone::new );
	public static final RegistryObject< InfestedEndStone > INFESTED_END_STONE = BLOCKS.create( "infested_end_stone", InfestedEndStone::new );

	// Items
	public static final RegistryObject< FragileEndStone.Item > FRAGILE_END_STONE_ITEM = ITEMS.create( "fragile_end_stone", FragileEndStone.Item::new );
	public static final RegistryObject< InfestedEndStone.Item > INFESTED_END_STONE_ITEM = ITEMS.create( "infested_end_stone", InfestedEndStone.Item::new );

	// Items (fake)
	static {
		ITEMS.create( "advancement_normal", FakeItem::new );
		ITEMS.create( "advancement_expert", FakeItem::new );
		ITEMS.create( "advancement_master", FakeItem::new );
	}

	// Effects
	public static final RegistryObject< BleedingEffect > BLEEDING = MOB_EFFECTS.create( "bleeding", BleedingEffect::new );
	public static final RegistryObject< BleedingImmunityEffect > BLEEDING_IMMUNITY = MOB_EFFECTS.create( "bleeding_immunity", BleedingImmunityEffect::new );

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
	}

	private static void setDefaultEmitters( OnGameInitialized data ) {
		ParticleEmitter.setDefault( BLOOD_PARTICLE.get(), new ParticleEmitter.Properties( ParticleEmitter.offset( 0.5f ), ParticleEmitter.speed( 0.025f, 0.075f ) ) );
	}

	private MajruszsDifficulty() {}

	@OnlyIn( Dist.CLIENT )
	public static class Client {
		static {
			OnParticlesRegistered.listen( data->data.register( BLOOD_PARTICLE.get(), BloodParticle.Factory::new ) );
		}
	}
}
