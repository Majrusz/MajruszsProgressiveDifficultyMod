package com.majruszs_difficulty.generation;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.mlib.MajruszLibrary;
import net.minecraft.block.Blocks;
import net.minecraft.item.CompassItem;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;

import java.util.ArrayList;
import java.util.List;

/** Generating naturally new ores in game. */
public class OreGeneration {
	public static final List< ConfiguredFeature< ?, ? > > END_ORES = new ArrayList<>();

	/** Register generation for ores. */
	public static void registerOres() {
		ConfiguredFeature< ?, ? > endShardOreSmallGeneration = register( "end_shard_ore_small", Feature.ORE.withConfiguration(
			new OreFeatureConfig( new BlockMatchRuleTest( Blocks.END_STONE ), Instances.END_SHARD_ORE.getDefaultState(), 2 ) )
			.range( 128 )
			.square()
			.func_242731_b( 16 )
		);
		ConfiguredFeature< ?, ? > endShardOreLargeGeneration = register( "end_shard_ore_large", Feature.ORE.withConfiguration(
			new OreFeatureConfig( new BlockMatchRuleTest( Blocks.END_STONE ), Instances.END_SHARD_ORE.getDefaultState(), 3 ) )
			.range( 128 )
			.square()
			.func_242731_b( 8 )
		);
		ConfiguredFeature< ?, ? > infestedEndStoneGeneration = register( "infested_end_stone", Feature.ORE.withConfiguration(
			new OreFeatureConfig( new BlockMatchRuleTest( Blocks.END_STONE ), Instances.INFESTED_END_STONE.getDefaultState(), 16 ) )
			.range( 128 )
			.square()
			.func_242731_b( 32 )
		);

		END_ORES.add( endShardOreSmallGeneration );
		END_ORES.add( endShardOreLargeGeneration );
		END_ORES.add( infestedEndStoneGeneration );
	}

	/** Register generation config. */
	private static < FeatureConfigType extends IFeatureConfig > ConfiguredFeature< FeatureConfigType, ? > register( String name,
		ConfiguredFeature< FeatureConfigType, ? > configuredFeature
	) {
		return Registry.register( WorldGenRegistries.CONFIGURED_FEATURE, MajruszsDifficulty.MOD_ID + ":" + name, configuredFeature );
	}
}
