package com.majruszs_difficulty.events;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.LivingEntity;

/** Class representing base feature that depends on chance and can be disabled. */
public abstract class FeatureBase {
	protected final GameState.State minimumState;
	protected final boolean shouldChanceBeMultipliedByCRD; // CRD = Clamped Regional Difficulty

	public FeatureBase( GameState.State minimumState, boolean shouldChanceBeMultipliedByCRD ) {
		this.minimumState = minimumState;
		this.shouldChanceBeMultipliedByCRD = shouldChanceBeMultipliedByCRD;
	}

	/** Checking if event is not disabled by the player. */
	protected abstract boolean isEnabled();

	/** Returns chance of applying event on entity. */
	protected abstract double getChance();

	/** Calculating final chance. (after applying clamped regional difficulty if needed) */
	protected double calculateChance( LivingEntity target ) {
		return getChance() * ( this.shouldChanceBeMultipliedByCRD ? MajruszsHelper.getClampedRegionalDifficulty( target ) : 1.0 );
	}
}
