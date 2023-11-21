package com.majruszsdifficulty;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.GenerationStep;

import java.util.function.Predicate;

public class Initializer implements ModInitializer {
	private static final Predicate< BiomeSelectionContext > IS_END = context->context.canGenerateIn( LevelStem.END );

	@Override
	public void onInitialize() {
		MajruszsDifficulty.HELPER.register();

		BiomeModifications.addFeature( IS_END, GenerationStep.Decoration.UNDERGROUND_ORES, MajruszsDifficulty.PlacedFeatures.ENDERIUM_ORE );
		BiomeModifications.addFeature( IS_END, GenerationStep.Decoration.UNDERGROUND_ORES, MajruszsDifficulty.PlacedFeatures.ENDERIUM_ORE_LARGE );
		BiomeModifications.addFeature( IS_END, GenerationStep.Decoration.UNDERGROUND_ORES, MajruszsDifficulty.PlacedFeatures.FRAGILE_END_STONE );
		BiomeModifications.addFeature( IS_END, GenerationStep.Decoration.UNDERGROUND_ORES, MajruszsDifficulty.PlacedFeatures.FRAGILE_END_STONE_LARGE );
		BiomeModifications.addFeature( IS_END, GenerationStep.Decoration.UNDERGROUND_ORES, MajruszsDifficulty.PlacedFeatures.INFESTED_END_STONE );
	}
}
