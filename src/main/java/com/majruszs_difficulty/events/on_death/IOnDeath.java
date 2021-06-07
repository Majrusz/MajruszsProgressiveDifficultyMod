package com.majruszs_difficulty.events.on_death;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

/** Interface for all events that do something on certain entity death. */
public interface IOnDeath {
	/** Called when all requirements were met. */
	void onExecute( LivingEntity entity, ServerWorld world );

	/** Checking if all conditions were met. */
	boolean shouldBeExecuted( LivingEntity entity );
}
