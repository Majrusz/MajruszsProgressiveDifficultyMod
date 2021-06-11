package com.majruszs_difficulty.features.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.items.WitherSwordItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;

import javax.annotation.Nullable;

/** Making Wither Sword attacks inflict Wither II. */
public class WitherSwordOnAttack extends WhenDamagedApplyEffectBase {
	private static final String CONFIG_NAME = "WitherSword";
	private static final String CONFIG_COMMENT = "Wither Sword inflicts Wither II.";

	public WitherSwordOnAttack() {
		super( CONFIG_NAME, CONFIG_COMMENT, 1.0, 6.0, GameState.State.NORMAL, false, Effects.WITHER );
	}

	/** Checks if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		boolean isHoldingWitherSword = attacker != null && attacker.getHeldItemMainhand()
			.getItem() instanceof WitherSwordItem;

		return isHoldingWitherSword && super.shouldBeExecuted( attacker, target, damageSource );
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return 1;
	}
}
