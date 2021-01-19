package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.ConfigHandlerOld.Config;
import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.items.WitherSwordItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;

import javax.annotation.Nullable;

/** Making Wither Sword attacks inflict Wither II. */
public class WitherSwordOnAttack extends WhenDamagedApplyEffectBase {
	public WitherSwordOnAttack() {
		super( GameState.State.NORMAL, false, Effects.WITHER );
	}

	/** Checking if all conditions were met. */
	@Override
	protected boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		boolean isHoldingWitherSword = attacker != null && attacker.getHeldItemMainhand()
			.getItem() instanceof WitherSwordItem;

		return isHoldingWitherSword && super.shouldBeExecuted( attacker, target, damageSource );
	}

	@Override
	protected boolean isEnabled() {
		return true;
	}

	@Override
	protected double getChance() {
		return 1.0;
	}

	@Override
	protected int getDurationInTicks( Difficulty difficulty ) {
		return Config.getDurationInSeconds( Config.Durations.WITHER_SWORD_EFFECT );
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return 1;
	}
}
