package com.majruszsdifficulty.features.when_damaged;

import com.majruszsdifficulty.GameState;
import com.mlib.Utility;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Phantom;

import javax.annotation.Nullable;

/** Makes Phantom attacks have a chance to set levitation on enemies. */
public class PhantomLevitationOnAttack extends WhenDamagedApplyStackableEffectBase {
	private static final String CONFIG_NAME = "PhantomLevitation";
	private static final String CONFIG_COMMENT = "Phantom inflicts levitation.";

	public PhantomLevitationOnAttack() {
		super( CONFIG_NAME,
			CONFIG_COMMENT,
			0.5,
			6.0,
			GameState.State.MASTER,
			true,
			MobEffects.LEVITATION,
			false,
			true,
			1,
			Utility.secondsToTicks( 60.0 )
		);
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return attacker instanceof Phantom && super.shouldBeExecuted( attacker, target, damageSource );
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return 0;
	}
}
