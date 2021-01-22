package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.Instances;
import com.mlib.effects.EffectHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
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

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return Instances.BLEEDING.getAmplifier();
	}

	/** Applying invisible bleeding effect instead of standard one. */
	@Override
	protected void applyEffect( LivingEntity target, Effect effect, Difficulty difficulty ) {
		EffectInstance invisibleEffect = new EffectInstance( effect, getDurationInTicks( difficulty ), getAmplifier( difficulty ), false, false );
		EffectHelper.applyEffectIfPossible( target, invisibleEffect );
	}

	/** Calculating final chance. (after applying clamped regional difficulty and armor multipliers) */
	protected double calculateChance( LivingEntity target ) {
		return Instances.BLEEDING.getChanceMultiplierDependingOnArmor( target ) * super.calculateChance( target );
	}
}