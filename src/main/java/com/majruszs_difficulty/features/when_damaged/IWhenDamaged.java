package com.majruszs_difficulty.features.when_damaged;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import javax.annotation.Nullable;

public interface IWhenDamaged {
	/**
	 Function called when entity was damaged.

	 @param attacker Entity that dealt damage.
	 @param target Entity target that was attacked.
	 @param event More information about event.
	 */
	void whenDamaged( @Nullable LivingEntity attacker, LivingEntity target, LivingHurtEvent event );

	/** Checks if all conditions were met. */
	boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource );
}
