package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

/** Base class representing event on which entity was damaged. */
public abstract class WhenDamagedBase {
	protected final GameState.State minimumState;
	protected final boolean shouldChanceBeMultipliedByCRD; // CRD = Clamped Regional Difficulty

	public WhenDamagedBase( GameState.State minimumState, boolean shouldChanceBeMultipliedByCRD ) {
		this.minimumState = minimumState;
		this.shouldChanceBeMultipliedByCRD = shouldChanceBeMultipliedByCRD;
	}

	/**
	 Function called when entity was damaged.

	 @param target Entity target that was attacked.
	 */
	public abstract void whenDamaged( LivingEntity target );

	/** Checking if all conditions were met. */
	protected boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		if( !GameState.atLeast( this.minimumState ) )
			return false;

		if( !( target.world instanceof ServerWorld ) )
			return false;

		return isEnabled();
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