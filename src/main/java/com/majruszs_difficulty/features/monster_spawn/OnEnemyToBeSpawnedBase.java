package com.majruszs_difficulty.features.monster_spawn;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.features.ChanceFeatureBase;
import com.mlib.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.server.ServerWorld;

/** Base class for handling enemy spawn events. */
public abstract class OnEnemyToBeSpawnedBase extends ChanceFeatureBase implements IOnSpawn {
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
	public boolean shouldBeExecuted( LivingEntity entity ) {
		if( !GameState.atLeast( this.minimumState ) || !( entity.world instanceof ServerWorld ) || !isEnabled() || !( entity instanceof MobEntity ) )
			return false;

		return Random.tryChance( calculateChance( entity ) );
	}
}
