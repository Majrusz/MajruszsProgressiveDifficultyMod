package com.majruszsdifficulty.events.when_damaged;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.DamageSource;

import javax.annotation.Nullable;

/** Making thrown trident inflict bleeding on enemies. */
public class ThrownTridentBleedingOnHurt extends WhenDamagedApplyBleedingBase {
	private static final String CONFIG_NAME = "TridentBleeding";
	private static final String CONFIG_COMMENT = "Thrown trident cause bleeding on target.";

	public ThrownTridentBleedingOnHurt() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.5, 30.0 );
	}

	/** Checking if all conditions were met. */
	@Override
	protected boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return damageSource.getImmediateSource() instanceof TridentEntity && super.shouldBeExecuted( attacker, target, damageSource );
	}
}