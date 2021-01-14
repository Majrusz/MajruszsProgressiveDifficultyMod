package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.events.FeatureBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

/** Base class for handling enemy spawn events. */
public abstract class OnEnemyToBeSpawnedBase extends FeatureBase {
	protected boolean shouldBeCancelled = false;

	public OnEnemyToBeSpawnedBase( GameState.State minimumState, boolean shouldChanceBeMultipliedByCRD ) {
		super( minimumState, shouldChanceBeMultipliedByCRD );
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

		return MajruszsHelper.tryChance( calculateChance( entity ) );
	}
}
