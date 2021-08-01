package com.majruszs_difficulty.features.monster_spawn;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public interface IOnSpawn {
	/** Called when all requirements were met. */
	void onExecute( LivingEntity entity, ServerLevel world );

	/** Returns whether spawning current entity should be cancelled. */
	boolean shouldSpawnBeCancelled();

	/** Checking if all conditions were met. */
	boolean shouldBeExecuted( LivingEntity entity );
}
