package com.majruszsdifficulty.features.monster_spawn;

import com.majruszsdifficulty.GameState;
import com.majruszsdifficulty.features.ChanceFeatureBase;
import com.mlib.Random;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

/** Base class for handling enemy spawn events. */
public abstract class OnEnemyToBeSpawnedBase extends ChanceFeatureBase implements IOnSpawn {
	protected boolean shouldBeCancelled = false;

	public OnEnemyToBeSpawnedBase( String configName, String configComment, double defaultChance, GameState.State minimumState, boolean shouldChanceBeMultipliedByCRD
	) {
		super( configName, configComment, defaultChance, minimumState, shouldChanceBeMultipliedByCRD );
	}

	/** Called when all requirements were met. */
	public abstract void onExecute( LivingEntity entity, ServerLevel world );

	/** Returns whether spawning current entity should be cancelled. */
	public boolean shouldSpawnBeCancelled() {
		return this.shouldBeCancelled;
	}

	/** Checking if all conditions were met. */
	public boolean shouldBeExecuted( LivingEntity entity ) {
		if( !GameState.atLeast( this.minimumState ) || !( entity.level instanceof ServerLevel ) || !isEnabled() || !( entity instanceof Mob ) )
			return false;

		return Random.tryChance( calculateChance( entity ) );
	}
}
