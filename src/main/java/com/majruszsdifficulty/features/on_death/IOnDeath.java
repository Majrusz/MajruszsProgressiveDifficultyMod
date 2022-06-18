package com.majruszsdifficulty.features.on_death;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import javax.annotation.Nullable;

/** Interface for all events that do something on certain entity death. */
public interface IOnDeath {
	/** Called when all requirements were met. */
	void onExecute( @Nullable LivingEntity attacker, LivingEntity target, LivingDeathEvent event );

	/** Checking if all conditions were met. */
	boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource );
}
