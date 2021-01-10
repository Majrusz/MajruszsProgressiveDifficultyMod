package com.majruszs_difficulty.events.on_attack;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;

/** Base class representing event on which entity was attacked. */
public abstract class OnAttackBase {
	protected final Class< ? extends LivingEntity > entityCausingEffect;
	protected final GameState.Mode minimumMode;
	protected final boolean shouldChanceBeMultipliedByCRD; // CRD = Clamped Regional Difficulty

	public OnAttackBase( Class< ? extends LivingEntity > entityCausingEffect, GameState.Mode minimumMode, boolean shouldChanceBeMultipliedByCRD ) {
		this.entityCausingEffect = entityCausingEffect;
		this.minimumMode = minimumMode;
		this.shouldChanceBeMultipliedByCRD = shouldChanceBeMultipliedByCRD;
	}

	/** Function called when entity attacked second entity and all conditions are met.

	 @param attacker Attacker.
	 @param target Target.
	 @param damageSource Source from which the target was attacked.
	 */
	public abstract void onAttack( LivingEntity attacker, LivingEntity target, DamageSource damageSource );

	/** Checking if all conditions are not met. */
	protected boolean shouldBeExecuted( LivingEntity attacker ) {
		if( !( this.entityCausingEffect.isInstance( attacker ) ) )
			return false;

		if( !GameState.atLeast( this.minimumMode ) )
			return false;

		if( !( attacker.world instanceof ServerWorld ) )
			return false;

		return isEnabled();
	}

	/** Checking if event is not disabled by the player. */
	protected abstract boolean isEnabled();

	/** Returns chance of applying event on entity. */
	protected abstract double getChance();

	/** Calculating final chance. (after applying clamped regional difficulty if needed) */
	protected double calculateChance( LivingEntity attacker ) {
		return getChance() * ( this.shouldChanceBeMultipliedByCRD ? MajruszsHelper.getClampedRegionalDifficulty( attacker ) : 1.0 );
	}
}
