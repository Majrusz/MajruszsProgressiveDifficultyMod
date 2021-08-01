package com.majruszs_difficulty.features.when_damaged;

import com.majruszs_difficulty.GameState;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;

import javax.annotation.Nullable;

/** Makes Slime attacks have a chance to slow enemies. */
public class SlimeSlownessOnAttack extends WhenDamagedApplyEffectBase {
	private static final String CONFIG_NAME = "SlimeSlowness";
	private static final String CONFIG_COMMENT = "Slime inflicts Slowness.";

	public SlimeSlownessOnAttack() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.5, 6.0, GameState.State.EXPERT, true, MobEffects.MOVEMENT_SLOWDOWN );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return attacker instanceof Slime && super.shouldBeExecuted( attacker, target, damageSource );
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return 0;
	}
}