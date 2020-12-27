package com.majruszs_difficulty;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ConfigHandler {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static ForgeConfigSpec CONFIG_SPEC;
	public static final String FILENAME = "majruszs-difficulty.toml";

	public static class Config {
		public static class Features {
			public static ForgeConfigSpec.BooleanValue FALL_DAMAGE, CREEPER_CHARGED, CREEPER_EFFECTS, SKELETON_GROUPS, ZOMBIE_GROUPS, ILLUSIONER_SPAWNING, GIANT_SPAWNING, PILLAGER_WOLF_SPAWNING, PIGLIN_GROUPS, PILLAGER_GROUPS, SPIDER_POISON, EVOKER_TOTEM, WITHER_SKELETON_SWORD;
		}

		public static class Chances {
			public static ForgeConfigSpec.DoubleValue CREEPER_CHARGED, CREEPER_EFFECTS, ENEMY_GROUPS, SPIDER_POISON;
		}

		public static boolean isDisabled( ForgeConfigSpec.BooleanValue config ) {
			return !config.get();
		}

		public static double getChance( ForgeConfigSpec.DoubleValue chance ) {
			return chance.get();
		}
	}

	public static void register( final ModLoadingContext context ) {
		ConfigHandler.load();

		context.registerConfig( ModConfig.Type.COMMON, CONFIG_SPEC, FILENAME );
	}

	private static void load() {
		BUILDER.comment( "Remember to restart your client/server after changing config!" );

		BUILDER.push( "Features" );
		Config.Features.FALL_DAMAGE = createConfigSpecForBoolean( "fall_damage", "", true );
		Config.Features.CREEPER_CHARGED = createConfigSpecForBoolean( "creeper_charged", "", true );
		Config.Features.CREEPER_EFFECTS = createConfigSpecForBoolean( "creeper_effects", "", true );
		Config.Features.SKELETON_GROUPS = createConfigSpecForBoolean( "skeleton_groups", "", true );
		Config.Features.ZOMBIE_GROUPS = createConfigSpecForBoolean( "zombie_groups", "", true );
		Config.Features.ILLUSIONER_SPAWNING = createConfigSpecForBoolean( "illusioner_spawning", "", true );
		Config.Features.GIANT_SPAWNING = createConfigSpecForBoolean( "giant_spawning", "", true );
		Config.Features.PILLAGER_WOLF_SPAWNING = createConfigSpecForBoolean( "pillager_wolf_spawning", "", true );
		Config.Features.PIGLIN_GROUPS = createConfigSpecForBoolean( "piglin_groups", "", true );
		Config.Features.PILLAGER_GROUPS = createConfigSpecForBoolean( "pillager_groups", "", true );
		Config.Features.SPIDER_POISON = createConfigSpecForBoolean( "spider_poison", "", true );
		Config.Features.EVOKER_TOTEM = createConfigSpecForBoolean( "evoker_totem", "", true );
		Config.Features.WITHER_SKELETON_SWORD = createConfigSpecForBoolean( "wither_skeleton_sword", "", true );
		BUILDER.pop();

		BUILDER.push( "Chances" );
		Config.Chances.CREEPER_CHARGED = createConfigSpecForDouble( "creeper_charged_chance", "", 0.125, 0.0, 1.0 );
		Config.Chances.CREEPER_EFFECTS = createConfigSpecForDouble( "creeper_effects_chance", "", 0.375, 0.0, 1.0 );
		Config.Chances.ENEMY_GROUPS = createConfigSpecForDouble( "enemy_groups_chance", "", 0.25, 0.0, 1.0 );
		Config.Chances.SPIDER_POISON = createConfigSpecForDouble( "spider_poison_chance", "", 0.25, 0.0, 1.0 );
		BUILDER.pop();
	}

	private static ForgeConfigSpec.BooleanValue createConfigSpecForBoolean( String name, String comment, boolean defaultValue ) {
		return BUILDER.comment( comment )
			.worldRestart()
			.define( name, defaultValue );
	}

	private static ForgeConfigSpec.DoubleValue createConfigSpecForDouble( String name, String comment, double defaultValue, double min, double max ) {
		return BUILDER.comment( comment )
			.worldRestart()
			.defineInRange( name, defaultValue, min, max );
	}
}
