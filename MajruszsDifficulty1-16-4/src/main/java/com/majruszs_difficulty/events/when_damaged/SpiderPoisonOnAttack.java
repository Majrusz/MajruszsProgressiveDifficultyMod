package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.ConfigHandler.Config;
import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;

import javax.annotation.Nullable;

/** Making Spider attacks have a chance to poison enemies. */
public class SpiderPoisonOnAttack extends WhenDamagedApplyEffectBase {
	public SpiderPoisonOnAttack() {
		super( GameState.Mode.NORMAL, true, Effects.POISON );
	}

	/** Checking if all conditions were met. */
	@Override
	protected boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return attacker instanceof SpiderEntity && super.shouldBeExecuted( attacker, target, damageSource );
	}

	@Override
	protected boolean isEnabled() {
		return !Config.isDisabled( Config.Features.SPIDER_POISON );
	}

	@Override
	protected double getChance() {
		return Config.getChance( Config.Chances.SPIDER_POISON );
	}

	@Override
	protected int getDurationInTicks( Difficulty difficulty ) {
		switch( difficulty ) {
			default:
				return 0;
			case NORMAL:
				return MajruszsHelper.secondsToTicks( 7.0 );
			case HARD:
				return MajruszsHelper.secondsToTicks( 15.0 );
		}
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return 0;
	}
}