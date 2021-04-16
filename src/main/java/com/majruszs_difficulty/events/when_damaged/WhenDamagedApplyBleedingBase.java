package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.effects.BleedingEffect.BleedingEffectInstance;
import com.mlib.effects.EffectHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;

import javax.annotation.Nullable;

/** Base class representing event on which enemies will receive bleeding after being attacked. */
public abstract class WhenDamagedApplyBleedingBase extends WhenDamagedApplyEffectBase {
	public WhenDamagedApplyBleedingBase( String configName, String configComment, double defaultChance, double defaultDurationInSeconds ) {
		super( configName, configComment, defaultChance, defaultDurationInSeconds, GameState.State.NORMAL, false, Instances.BLEEDING );
	}

	/** Checking if all conditions were met. */
	@Override
	protected boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return Instances.BLEEDING.mayBleed( target ) && super.shouldBeExecuted( attacker, target, damageSource );
	}

	/**
	 Applying effect on entity directly. (if possible, because enemy may be immune for example)

	 @param target     Entity who will get effect.
	 @param effect     Effect type to apply.
	 @param difficulty Current world difficulty.
	 */
	@Override
	protected void applyEffect( @Nullable LivingEntity attacker, LivingEntity target, Effect effect, Difficulty difficulty ) {
		BleedingEffectInstance effectInstance = new BleedingEffectInstance( getDurationInTicks( difficulty ), getAmplifier( difficulty ), false, true,
			attacker
		);

		EffectHelper.applyEffectIfPossible( target, effectInstance );
	}

	/** Applying invisible bleeding effect instead of standard one. */

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return Instances.BLEEDING.getAmplifier();
	}

	/** Calculating final chance. (after applying clamped regional difficulty and armor multipliers) */
	protected double calculateChance( LivingEntity target ) {
		return Instances.BLEEDING.getChanceMultiplierDependingOnArmor( target ) * super.calculateChance( target );
	}
}