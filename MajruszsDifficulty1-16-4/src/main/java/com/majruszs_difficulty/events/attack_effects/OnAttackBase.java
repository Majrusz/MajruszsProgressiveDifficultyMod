package com.majruszs_difficulty.events.attack_effects;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.GameState.Mode;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;

/** Base attack class representing event on which enemies will */
public abstract class OnAttackBase {
	protected final Class< ? extends LivingEntity > entityCausingEffect;
	protected final Mode minimumMode;
	protected final boolean shouldBeMultipliedByCRD; // CRD = Clamped Regional Difficulty
	protected final Effect[] effects;

	public OnAttackBase( Class< ? extends LivingEntity > entityCausingEffect, Mode minimumMode, boolean shouldBeMultipliedByCRD, Effect[] effects ) {
		this.entityCausingEffect = SpiderEntity.class;
		this.minimumMode = minimumMode;
		this.shouldBeMultipliedByCRD = shouldBeMultipliedByCRD;
		this.effects = effects;
	}

	public OnAttackBase( Class< ? extends LivingEntity > entityCausingEffect, Mode minimumMode, boolean shouldBeMultipliedByCRD, Effect effect ) {
		this( entityCausingEffect, minimumMode, shouldBeMultipliedByCRD, new Effect[]{ effect } );
	}

	/** Trying to apply all negative effects on entity. */
	public void tryToApplyEffect( LivingEntity attacker, LivingEntity target ) {
		if( !shouldBeExecuted( attacker ) )
			return;

		ServerWorld world = ( ServerWorld )attacker.getEntityWorld();

		for( Effect effect : this.effects ) {
			if( calculateChance( target, world ) < MajruszsDifficulty.RANDOM.nextDouble() )
				continue;

			EffectInstance effectInstance = new EffectInstance( effect, getDurationInTicks(), getAmplifier() );
			if( target.isPotionApplicable( effectInstance ) )
				target.addPotionEffect( effectInstance );
		}
	}

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

	/**
	 Returns chance of applying negative effect on entity.

	 @param difficulty Current game difficulty. (peaceful, easy, normal, hard)
	 */
	protected abstract double getChance( Difficulty difficulty );

	/**
	 Returns the duration in ticks of the effect.

	 @param difficulty Current game difficulty. (peaceful, easy, normal, hard)
	 */
	protected abstract int getDurationInTicks( Difficulty difficulty );

	/**
	 Returns the level of the effect.

	 @param difficulty Current game difficulty. (peaceful, easy, normal, hard)
	 */
	protected abstract int getAmplifier( Difficulty difficulty );

	/** Calculating final effect chance. (after applying clamped regional difficulty if needed) */
	private double calculateChance( LivingEntity attacker, ServerWorld world ) {
		return getChance( world.getDifficulty() ) * ( this.shouldBeMultipliedByCRD ? MajruszsHelper.getClampedRegionalDifficulty( attacker, world ) : 1.0 );
	}
}
