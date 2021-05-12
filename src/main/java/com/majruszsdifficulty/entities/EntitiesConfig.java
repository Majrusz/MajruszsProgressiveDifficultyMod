package com.majruszsdifficulty.entities;

import com.majruszsdifficulty.config.EntityConfig;
import com.mlib.config.DoubleConfig;

/** Handling configs for all custom entities. */
public class EntitiesConfig {
	public final EliteSkeletonConfig eliteSkeleton;
	public final EntityConfig giant;
	public final EntityConfig illusioner;
	public final EntityConfig pillagerWolf;
	public final EntityConfig skyKeeper;

	public EntitiesConfig() {
		this.eliteSkeleton = new EliteSkeletonConfig();
		this.giant = new EntityConfig( "Giant" );
		this.illusioner = new EntityConfig( "Illusioner" );
		this.pillagerWolf = new EntityConfig( "PillagerWolf" );
		this.skyKeeper = new EntityConfig( "SkyKeeper" );
	}

	public static class EliteSkeletonConfig extends EntityConfig {
		public final DoubleConfig tippedArrowChance;

		public EliteSkeletonConfig() {
			super( "EliteSkeleton" );

			String comment = "Chance for Elite Skeleton to use Tipped Arrow instead of simple Arrow. (scaled by Clamped Regional Difficulty)";
			this.tippedArrowChance = new DoubleConfig( "tipped_arrow_chance", comment, false, 0.75, 0.0, 1.0 );
			this.entityGroup.addConfig( this.tippedArrowChance );
		}
	}
}
