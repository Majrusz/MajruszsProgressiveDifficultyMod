package com.majruszs_difficulty.features.on_death;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import javax.annotation.Nullable;

/** Interface for all events that do something on certain entity death. */
public interface IOnDeath {
	/** Called when all requirements were met. */
	void onExecute( @Nullable LivingEntity attacker, LivingEntity target, LivingDeathEvent event );

	/** Checking if all conditions were met. */
	boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource );
}
