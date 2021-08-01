package com.majruszs_difficulty.generation;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;

import java.util.ArrayList;
import java.util.List;

/** Generating naturally new ores in game. */
public class OreGeneration {
	public static final List< ConfiguredFeature< ?, ? > > END_ORES = new ArrayList<>();

	/** Register generation for ores. */
	public static void registerOres() {
		ConfiguredFeature< ?, ? > endShardOreSmallGeneration = register( "end_shard_ore_small",
			Feature.ORE.configured( new OreConfiguration( new BlockMatchTest( Blocks.END_STONE ), Instances.END_SHARD_ORE.defaultBlockState(), 2 ) )
				.rangeUniform( VerticalAnchor.bottom(), VerticalAnchor.absolute( 128 ) )
				.squared()
				.count( 16 )
		);
		ConfiguredFeature< ?, ? > endShardOreLargeGeneration = register( "end_shard_ore_large",
			Feature.ORE.configured( new OreConfiguration( new BlockMatchTest( Blocks.END_STONE ), Instances.END_SHARD_ORE.defaultBlockState(), 3 ) )
				.rangeUniform( VerticalAnchor.bottom(), VerticalAnchor.absolute( 128 ) )
				.squared()
				.count( 8 )
		);
		ConfiguredFeature< ?, ? > infestedEndStoneGeneration = register( "infested_end_stone", Feature.ORE.configured(
			new OreConfiguration( new BlockMatchTest( Blocks.END_STONE ), Instances.INFESTED_END_STONE.defaultBlockState(), 4 ) )
			.rangeUniform( VerticalAnchor.bottom(), VerticalAnchor.absolute( 128 ) )
			.squared()
			.count( 64 ) );

		if( Instances.END_SHARD_ORE.isEnabled() ) {
			END_ORES.add( endShardOreSmallGeneration );
			END_ORES.add( endShardOreLargeGeneration );
		}
		END_ORES.add( infestedEndStoneGeneration );
	}

	/** Register generation config. */
	private static < FeatureConfigType extends FeatureConfiguration > ConfiguredFeature< FeatureConfigType, ? > register( String name,
		ConfiguredFeature< FeatureConfigType, ? > configuredFeature
	) {
		return Registry.register( BuiltinRegistries.CONFIGURED_FEATURE, MajruszsDifficulty.MOD_ID + ":" + name, configuredFeature );
	}
}
