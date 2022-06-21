package com.majruszsdifficulty.features.when_damaged;

import com.majruszsdifficulty.GameState;
import com.mlib.Utility;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/** Applying blindness when player is drowning. */
public class NauseaAndWeaknessWhenDrowning extends WhenDamagedApplyStackableEffectBase {
	private static final String CONFIG_NAME = "NauseaAndWeaknessWhenDrowning";
	private static final String CONFIG_COMMENT = "Drowning inflicts Nausea and Drowning.";

	public NauseaAndWeaknessWhenDrowning() {
		super( CONFIG_NAME, CONFIG_COMMENT, 1.0, 5.0, GameState.State.NORMAL, false, new MobEffect[]{ MobEffects.CONFUSION, MobEffects.WEAKNESS
		}, false, true, 0, Utility.secondsToTicks( 10.0 ) );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return damageSource.equals( DamageSource.DROWN ) && super.shouldBeExecuted( attacker, target, damageSource );
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return 0;
	}
}
