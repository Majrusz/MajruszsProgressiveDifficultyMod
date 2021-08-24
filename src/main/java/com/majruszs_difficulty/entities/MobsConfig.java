package com.majruszs_difficulty.entities;

import com.majruszs_difficulty.config.MobConfig;
import com.mlib.config.DoubleConfig;

/** Handling configs for all custom mobs. */
public class MobsConfig {
	public final EliteSkeletonConfig eliteSkeleton;

	public MobsConfig() {
		this.eliteSkeleton = new EliteSkeletonConfig();
	}

	public static class EliteSkeletonConfig extends MobConfig {
		public final DoubleConfig tippedArrowChance;
		public final DoubleConfig quickShotChance;
		public final DoubleConfig multiShotChance;

		public EliteSkeletonConfig() {
			super( "EliteSkeleton" );

			String tippedComment = "Chance for Elite Skeleton to use Tipped Arrow instead of simple Arrow. (scaled by Clamped Regional Difficulty)";
			this.tippedArrowChance = new DoubleConfig( "tipped_arrow_chance", tippedComment, false, 0.75, 0.0, 1.0 );

			String quickComment = "Chance for quick shot. (series of 4 shots in the shorter interval) (scaled by Clamped Regional Difficulty)";
			this.quickShotChance = new DoubleConfig( "quick_shot_chance", quickComment, false, 0.5, 0.0, 1.0 );

			String multiComment = "Chance for shooting 3 arrows instead of 1. (scaled by Clamped Regional Difficulty)";
			this.multiShotChance = new DoubleConfig( "multi_shot_chance", multiComment, false, 0.5, 0.0, 1.0 );

			this.entityGroup.addConfigs( this.tippedArrowChance, this.quickShotChance, this.multiShotChance );
		}
	}
}
