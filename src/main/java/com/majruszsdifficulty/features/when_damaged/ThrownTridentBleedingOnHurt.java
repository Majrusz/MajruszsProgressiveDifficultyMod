package com.majruszsdifficulty.features.when_damaged;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;

import javax.annotation.Nullable;

/** Making thrown trident inflict bleeding on enemies. */
public class ThrownTridentBleedingOnHurt extends WhenDamagedApplyBleedingBaseOld {
	private static final String CONFIG_NAME = "TridentBleeding";
	private static final String CONFIG_COMMENT = "Thrown trident cause bleeding on target.";

	public ThrownTridentBleedingOnHurt() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.5, 30.0 );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return damageSource.getDirectEntity() instanceof ThrownTrident && super.shouldBeExecuted( attacker, target, damageSource );
	}
}