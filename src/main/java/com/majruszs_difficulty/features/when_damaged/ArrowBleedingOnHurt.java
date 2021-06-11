package com.majruszs_difficulty.events.when_damaged;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.DamageSource;

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
		return damageSource.getImmediateSource() instanceof ArrowEntity && super.shouldBeExecuted( attacker, target, damageSource );
	}
}