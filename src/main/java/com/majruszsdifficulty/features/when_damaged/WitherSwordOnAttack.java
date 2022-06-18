package com.majruszsdifficulty.features.when_damaged;

import com.majruszsdifficulty.GameState;
import com.majruszsdifficulty.items.WitherSwordItem;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/** Making Wither Sword attacks inflict Wither II. */
public class WitherSwordOnAttack extends WhenDamagedApplyEffectBase {
	private static final String CONFIG_NAME = "WitherSword";
	private static final String CONFIG_COMMENT = "Wither Sword inflicts Wither II.";

	public WitherSwordOnAttack() {
		super( CONFIG_NAME, CONFIG_COMMENT, 1.0, 6.0, GameState.State.NORMAL, false, MobEffects.WITHER );
	}

	/** Checks if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		boolean isHoldingWitherSword = attacker != null && attacker.getMainHandItem().getItem() instanceof WitherSwordItem;

		return isHoldingWitherSword && super.shouldBeExecuted( attacker, target, damageSource );
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return 1;
	}
}
