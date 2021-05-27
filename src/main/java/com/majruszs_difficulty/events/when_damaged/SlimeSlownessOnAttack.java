package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.GameState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;

import javax.annotation.Nullable;

/** Makes Slime attacks have a chance to slow enemies. */
public class SlimeSlownessOnAttack extends WhenDamagedApplyEffectBase {
	private static final String CONFIG_NAME = "SlimeSlowness";
	private static final String CONFIG_COMMENT = "Slime inflicts Slowness.";

	public SlimeSlownessOnAttack() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.5, 6.0, GameState.State.EXPERT, true, Effects.SLOWNESS );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return attacker instanceof SlimeEntity && super.shouldBeExecuted( attacker, target, damageSource );
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return 0;
	}
}