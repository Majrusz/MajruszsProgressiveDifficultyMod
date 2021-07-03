package com.majruszs_difficulty.entities;

import com.majruszs_difficulty.config.EntityConfig;
import com.mlib.config.DoubleConfig;

/** Handling configs for all custom entities. */
public class EntitiesConfig {
	public final EliteSkeletonConfig eliteSkeleton;
	public final EntityConfig giant;
	public final EntityConfig illusioner;
	public final EntityConfig pillagerWolf;
	public final EntityConfig skyKeeper;
	public final EntityConfig creeperling;
	public final EntityConfig parasite;

	public EntitiesConfig() {
		this.eliteSkeleton = new EliteSkeletonConfig();
		this.giant = new EntityConfig( "Giant" );
		this.illusioner = new EntityConfig( "Illusioner" );
		this.pillagerWolf = new EntityConfig( "PillagerWolf" );
		this.skyKeeper = new EntityConfig( "SkyKeeper" );
		this.creeperling = new EntityConfig( "Creeperling" );
		this.parasite = new EntityConfig( "Parasite" );
	}

	public static class EliteSkeletonConfig extends EntityConfig {
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
