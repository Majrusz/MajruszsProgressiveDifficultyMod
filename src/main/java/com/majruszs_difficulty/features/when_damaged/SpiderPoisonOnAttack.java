package com.majruszs_difficulty.features.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.entities.ParasiteEntity;
import com.mlib.TimeConverter;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Spider;

import javax.annotation.Nullable;

/** Making Spider attacks have a chance to poison enemies. */
public class SpiderPoisonOnAttack extends WhenDamagedApplyEffectBase {
	private static final String CONFIG_NAME = "SpiderPoison";
	private static final String CONFIG_COMMENT = "Spider inflicts poison.";

	public SpiderPoisonOnAttack() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.25, -1.0, GameState.State.EXPERT, true, MobEffects.POISON );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return attacker instanceof Spider && !( attacker instanceof ParasiteEntity ) && super.shouldBeExecuted( attacker, target, damageSource );
	}

	@Override
	protected int getDurationInTicks( Difficulty difficulty ) {
		switch( difficulty ) {
			default:
				return TimeConverter.secondsToTicks( 0.0 );
			case NORMAL:
				return TimeConverter.secondsToTicks( 7.0 );
			case HARD:
				return TimeConverter.secondsToTicks( 15.0 );
		}
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return 0;
	}
}