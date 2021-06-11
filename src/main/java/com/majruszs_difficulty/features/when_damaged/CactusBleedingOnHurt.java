package com.majruszs_difficulty.features.when_damaged;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;

import javax.annotation.Nullable;

/** Making Cactus inflict bleeding on enemies. */
public class CactusBleedingOnHurt extends WhenDamagedApplyBleedingBase {
	private static final String CONFIG_NAME = "CactusBleeding";
	private static final String CONFIG_COMMENT = "Touching cactus inflict bleeding.";

	public CactusBleedingOnHurt() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.5, 24.0 );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return damageSource.equals( DamageSource.CACTUS ) && super.shouldBeExecuted( attacker, target, damageSource );
	}
}