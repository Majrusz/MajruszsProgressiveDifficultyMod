package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.effects.BleedingEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;

import javax.annotation.Nullable;

/** Base class representing event on which enemies will receive bleeding after being attacked. */
public abstract class WhenDamagedApplyBleedingBase extends WhenDamagedApplyEffectBase {
	public WhenDamagedApplyBleedingBase() {
		super( GameState.Mode.NORMAL, false, BleedingEffect.instance );
	}

	/** Checking if all conditions were met. */
	@Override
	protected boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return BleedingEffect.mayBleed( target ) && super.shouldBeExecuted( attacker, target, damageSource );
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return BleedingEffect.getAmplifier();
	}

	/** Calculating final chance. (after applying clamped regional difficulty and armor multipliers) */
	protected double calculateChance( LivingEntity target ) {
		return BleedingEffect.getChanceMultiplierDependingOnArmor( target ) * super.calculateChance( target );
	}
}