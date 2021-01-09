package com.majruszs_difficulty.events.attack_effects;

import com.majruszs_difficulty.GameState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

/** Base attack class representing event on which enemies will */
public abstract class OnAttackBase {
	protected final LivingEntity entityCausingEffect;
	protected final GameState minimumGameState;
	protected final boolean shouldBeMultipliedByClampedRegionalDifficulty;
	protected final Effect[] effects;

	public OnAttackBase( LivingEntity entityCausingEffect, GameState minimumGameState, boolean shouldBeMultipliedByClampedRegionalDifficulty, Effect[] effects ) {
		this.entityCausingEffect = entityCausingEffect;
		this.minimumGameState = minimumGameState;
		this.shouldBeMultipliedByClampedRegionalDifficulty = shouldBeMultipliedByClampedRegionalDifficulty;
		this.effects = effects;
	}

	public OnAttackBase( LivingEntity entityCausingEffect, GameState minimumGameState, boolean shouldBeMultipliedByClampedRegionalDifficulty, Effect effect ) {
		this( entityCausingEffect, minimumGameState, shouldBeMultipliedByClampedRegionalDifficulty, new Effect[]{ effect } );
	}

	/** Checking if all conditions are met. */
	protected boolean shouldBeCalled() {
		return isEnabled();
	}

	/** Checking if event is not disabled by the player. */
	protected abstract boolean isEnabled();

	/** Returns chance of applying negative effect on entity. */
	protected abstract double getChance();

	/** Returns the duration in ticks of the effect. */
	protected abstract int getDurationInTicks();

	/** Returns the level of the effect. */
	protected abstract int getAmplifier();
}
