package com.majruszsdifficulty.world;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class WorldGenHelper {
	public static ConfiguredFeature< ?, ? > getConfigured( Block targetBlock, RegistryObject< ? extends Block > block, int count,
		float airExposureDiscardChance
	) {
		List< OreConfiguration.TargetBlockState > target = List.of( OreConfiguration.target( new BlockMatchTest( targetBlock ), block.get()
			.defaultBlockState() ) );
		return new ConfiguredFeature<>( Feature.ORE, new OreConfiguration( target, count, airExposureDiscardChance ) );
	}

	public static ConfiguredFeature< ?, ? > getEndConfigured( RegistryObject< ? extends Block > block, int count, float airExposureDiscardChance ) {
		return getConfigured( Blocks.END_STONE, block, count, airExposureDiscardChance );
	}

	public static PlacedFeature getEndPlaced( RegistryObject< ConfiguredFeature< ?, ? > > configuredFeature, int count ) {
		Holder< ConfiguredFeature< ?, ? > > holder = configuredFeature.getHolder().get();
		return new PlacedFeature( holder, List.of( CountPlacement.of( count ), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome() ) );
	}
}
