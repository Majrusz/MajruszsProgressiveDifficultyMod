package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.ConfigHandler.Config;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;

import javax.annotation.Nullable;

/** Making Cactus inflict bleeding on enemies. */
public class CactusBleedingOnHurt extends WhenDamagedApplyBleedingBase {
	/** Checking if all conditions were met. */
	@Override
	protected boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return damageSource.equals( DamageSource.CACTUS ) && super.shouldBeExecuted( attacker, target, damageSource );
	}

	@Override
	protected boolean isEnabled() {
		return !Config.isDisabled( Config.Features.CACTUS_BLEEDING );
	}

	@Override
	protected double getChance() {
		return Config.getChance( Config.Chances.CACTUS_BLEEDING );
	}

	@Override
	protected int getDurationInTicks( Difficulty difficulty ) {
		return Config.getDurationInSeconds( Config.Durations.CACTUS_BLEEDING );
	}
}