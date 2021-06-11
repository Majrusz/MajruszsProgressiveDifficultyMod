package com.majruszs_difficulty.features.when_damaged;

import com.majruszs_difficulty.GameState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;

import javax.annotation.Nullable;

/** Makes Shulker attacks have a chance to blind enemies. */
public class ShulkerBlindnessOnAttack extends WhenDamagedApplyEffectBase {
	private static final String CONFIG_NAME = "ShulkerBlindness";
	private static final String CONFIG_COMMENT = "Shulker inflict Blindness.";

	public ShulkerBlindnessOnAttack() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.5, 5.0, GameState.State.MASTER, true, Effects.BLINDNESS );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return attacker instanceof ShulkerEntity && super.shouldBeExecuted( attacker, target, damageSource );
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return 0;
	}
}