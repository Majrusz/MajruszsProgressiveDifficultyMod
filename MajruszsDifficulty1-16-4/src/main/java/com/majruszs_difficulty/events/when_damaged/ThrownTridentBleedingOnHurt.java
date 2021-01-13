package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.ConfigHandler.Config;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;

import javax.annotation.Nullable;

/** Making thrown trident inflict bleeding on enemies. */
public class ThrownTridentBleedingOnHurt extends WhenDamagedApplyBleedingBase {
	/** Checking if all conditions were met. */
	@Override
	protected boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return damageSource.getImmediateSource() instanceof TridentEntity && super.shouldBeExecuted( attacker, target, damageSource );
	}

	@Override
	protected boolean isEnabled() {
		return !Config.isDisabled( Config.Features.THROWN_TRIDENT_BLEEDING );
	}

	@Override
	protected double getChance() {
		return Config.getChance( Config.Chances.THROWN_TRIDENT_BLEEDING );
	}

	@Override
	protected int getDurationInTicks( Difficulty difficulty ) {
		return Config.getDurationInSeconds( Config.Durations.THROWN_TRIDENT_BLEEDING );
	}
}