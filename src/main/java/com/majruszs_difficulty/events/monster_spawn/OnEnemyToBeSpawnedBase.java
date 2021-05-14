package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.events.ChanceFeatureBase;
import com.mlib.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

/** Base class for handling enemy spawn events. */
public abstract class OnEnemyToBeSpawnedBase extends ChanceFeatureBase {
	protected boolean shouldBeCancelled = false;

	public OnEnemyToBeSpawnedBase( String configName, String configComment, double defaultChance, GameState.State minimumState,
		boolean shouldChanceBeMultipliedByCRD
	) {
		super( configName, configComment, defaultChance, minimumState, shouldChanceBeMultipliedByCRD );
	}

	/** Called when all requirements were met. */
	public abstract void onExecute( LivingEntity entity, ServerWorld world );

	/** Returns whether spawning current entity should be cancelled. */
	public boolean shouldSpawnBeCancelled() {
		return this.shouldBeCancelled;
	}

	/** Checking if all conditions were met. */
	protected boolean shouldBeExecuted( LivingEntity entity ) {
		if( !GameState.atLeast( this.minimumState ) )
			return false;

		if( !( entity.world instanceof ServerWorld ) )
			return false;

		if( !isEnabled() )
			return false;

		return Random.tryChance( calculateChance( entity ) );
	}
}
