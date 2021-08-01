package com.majruszs_difficulty.features.when_damaged;

import com.majruszs_difficulty.GameState;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Shulker;

import javax.annotation.Nullable;

/** Makes Shulker attacks have a chance to blind enemies. */
public class ShulkerBlindnessOnAttack extends WhenDamagedApplyEffectBase {
	private static final String CONFIG_NAME = "ShulkerBlindness";
	private static final String CONFIG_COMMENT = "Shulker inflict Blindness.";

	public ShulkerBlindnessOnAttack() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.5, 5.0, GameState.State.MASTER, true, MobEffects.BLINDNESS );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return attacker instanceof Shulker && super.shouldBeExecuted( attacker, target, damageSource );
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return 0;
	}
}