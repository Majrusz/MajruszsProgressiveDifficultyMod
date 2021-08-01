package com.majruszs_difficulty.features.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.effects.BleedingEffect;
import com.majruszs_difficulty.effects.BleedingEffect.BleedingMobEffectInstance;
import com.mlib.effects.EffectHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/** Base class representing event on which enemies will receive bleeding after being attacked. */
public abstract class WhenDamagedApplyBleedingBase extends WhenDamagedApplyEffectBase {
	public WhenDamagedApplyBleedingBase( String configName, String configComment, double defaultChance, double defaultDurationInSeconds ) {
		super( configName, configComment, defaultChance, defaultDurationInSeconds, GameState.State.NORMAL, false, Instances.BLEEDING );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return Instances.BLEEDING.mayBleed(
			target ) && !( damageSource instanceof BleedingEffect.EntityBleedingDamageSource ) && super.shouldBeExecuted( attacker, target,
			damageSource
		);
	}

	/**
	 Applying effect on entity directly. (if possible, because enemy may be immune for example)

	 @param target     Entity who will get effect.
	 @param effect     MobEffect type to apply.
	 @param difficulty Current world difficulty.
	 */
	@Override
	protected void applyEffect( @Nullable LivingEntity attacker, LivingEntity target, MobEffect effect, Difficulty difficulty ) {
		BleedingMobEffectInstance effectInstance = new BleedingMobEffectInstance( getDurationInTicks( difficulty ), getAmplifier( difficulty ), false,
			true, attacker
		);

		EffectHelper.applyEffectIfPossible( target, effectInstance );
	}

	/** Applying invisible bleeding effect instead of standard one. */

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return Instances.BLEEDING.getAmplifier();
	}

	/** Calculating final chance. (after applying clamped regional difficulty and armor multipliers) */
	@Override
	public double calculateChance( LivingEntity target ) {
		return Instances.BLEEDING.getChanceMultiplierDependingOnArmor( target ) * super.calculateChance( target );
	}
}