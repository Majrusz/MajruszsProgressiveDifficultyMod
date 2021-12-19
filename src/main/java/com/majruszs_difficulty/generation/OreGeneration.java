package com.majruszs_difficulty.generation;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;

import java.util.ArrayList;
import java.util.List;

/** Generating naturally new ores in game. */
public class OreGeneration {
	public static final List< PlacedFeature > END_ORES = new ArrayList<>();

	/** Register generation for ores. */
	public static void registerOres() {
		ConfiguredFeature< ?, ? > endShardOreSmallGeneration = register( "end_shard_ore_small",
			Feature.ORE.configured( new OreConfiguration( new BlockMatchTest( Blocks.END_STONE ), Instances.END_SHARD_ORE.defaultBlockState(), 2 ) )
		);
		ConfiguredFeature< ?, ? > endShardOreLargeGeneration = register( "end_shard_ore_large",
			Feature.ORE.configured( new OreConfiguration( new BlockMatchTest( Blocks.END_STONE ), Instances.END_SHARD_ORE.defaultBlockState(), 3 ) )
		);
		ConfiguredFeature< ?, ? > infestedEndStoneGeneration = register( "infested_end_stone", Feature.ORE.configured(
			new OreConfiguration( new BlockMatchTest( Blocks.END_STONE ), Instances.INFESTED_END_STONE.defaultBlockState(), 4 ) ) );

		if( Instances.END_SHARD_ORE.isEnabled() ) {
			END_ORES.add( getEndOrePlacedFeature( endShardOreSmallGeneration, "end_shard_ore_small", 16 ) );
			END_ORES.add( getEndOrePlacedFeature( endShardOreLargeGeneration, "end_shard_ore_large", 8 ) );
		}
		END_ORES.add( getEndOrePlacedFeature( infestedEndStoneGeneration, "infested_end_stone", 64 ) );
	}

	/** Register generation config. */
	private static < FeatureConfigType extends FeatureConfiguration > ConfiguredFeature< FeatureConfigType, ? > register( String name,
		ConfiguredFeature< FeatureConfigType, ? > configuredFeature
	) {
		return Registry.register( BuiltinRegistries.CONFIGURED_FEATURE, MajruszsDifficulty.MOD_ID + ":" + name, configuredFeature );
	}

	private static PlacedFeature getEndOrePlacedFeature( ConfiguredFeature< ?, ? > feature, String name, int count ) {
		HeightRangePlacement heightPlacement = HeightRangePlacement.uniform( VerticalAnchor.bottom(), VerticalAnchor.absolute( 128 ) );
		CountPlacement countPlacement = CountPlacement.of( count );

		return PlacementUtils.register( name,
			feature.placed( List.of( countPlacement, InSquarePlacement.spread(), heightPlacement, BiomeFilter.biome() ) )
		);
	}
}
