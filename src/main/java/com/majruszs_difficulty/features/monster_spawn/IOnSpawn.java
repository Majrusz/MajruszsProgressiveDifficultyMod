package com.majruszs_difficulty.features.monster_spawn;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

public interface IOnSpawn {
	/** Called when all requirements were met. */
	void onExecute( LivingEntity entity, ServerWorld world );

	/** Returns whether spawning current entity should be cancelled. */
	boolean shouldSpawnBeCancelled();

	/** Checking if all conditions were met. */
	boolean shouldBeExecuted( LivingEntity entity );
}
