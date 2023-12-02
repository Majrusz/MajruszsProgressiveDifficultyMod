package com.majruszsdifficulty;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.GenerationStep;

import java.util.function.Predicate;

public class Initializer implements ModInitializer {
	private static final Predicate< BiomeSelectionContext > IS_OVERWORLD = context->context.canGenerateIn( LevelStem.OVERWORLD );
	private static final Predicate< BiomeSelectionContext > IS_NETHER = context->context.canGenerateIn( LevelStem.NETHER );
	private static final Predicate< BiomeSelectionContext > IS_END = context->context.canGenerateIn( LevelStem.END );

	@Override
	public void onInitialize() {
		MajruszsDifficulty.HELPER.register();

		BiomeModifications.addFeature( IS_END, GenerationStep.Decoration.UNDERGROUND_ORES, MajruszsDifficulty.ENDERIUM_ORE_PLACED_FEATURE );
		BiomeModifications.addFeature( IS_END, GenerationStep.Decoration.UNDERGROUND_ORES, MajruszsDifficulty.ENDERIUM_ORE_LARGE_PLACED_FEATURE );
		BiomeModifications.addFeature( IS_END, GenerationStep.Decoration.UNDERGROUND_ORES, MajruszsDifficulty.FRAGILE_END_STONE_PLACED_FEATURE );
		BiomeModifications.addFeature( IS_END, GenerationStep.Decoration.UNDERGROUND_ORES, MajruszsDifficulty.FRAGILE_END_STONE_LARGE_PLACED_FEATURE );
		BiomeModifications.addFeature( IS_END, GenerationStep.Decoration.UNDERGROUND_ORES, MajruszsDifficulty.INFESTED_END_STONE_PLACED_FEATURE );

		BiomeModifications.addSpawn( IS_OVERWORLD, MobCategory.MONSTER, MajruszsDifficulty.CURSED_ARMOR_ENTITY.get(), 30, 1, 3 );
		BiomeModifications.addSpawn( IS_OVERWORLD, MobCategory.MONSTER, MajruszsDifficulty.ILLUSIONER_ENTITY.get(), 8, 1, 1 );
		BiomeModifications.addSpawn( IS_OVERWORLD, MobCategory.MONSTER, MajruszsDifficulty.TANK_ENTITY.get(), 3, 1, 1 );
		BiomeModifications.addSpawn( IS_NETHER, MobCategory.MONSTER, MajruszsDifficulty.CERBERUS_ENTITY.get(), 1, 1, 1 );
	}
}
