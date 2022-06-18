package com.majruszsdifficulty.features.when_damaged;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;

import javax.annotation.Nullable;

/** Making arrows inflict bleeding on enemies. */
public class ArrowBleedingOnHurt extends WhenDamagedApplyBleedingBase {
	private static final String CONFIG_NAME = "ArrowBleeding";
	private static final String CONFIG_COMMENT = "Arrow inflicts bleeding.";

	public ArrowBleedingOnHurt() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.25, 24.0 );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return damageSource.getDirectEntity() instanceof Arrow && super.shouldBeExecuted( attacker, target, damageSource );
	}
}