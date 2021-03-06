package com.majruszs_difficulty.features.monster_spawn;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.goals.CreeperExplodeWallsGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.world.server.ServerWorld;

/** Adds new AI goals creeper on spawn. (makes the creeper destroy walls when near the player) */
public class AddAIToCreeperOnSpawn extends OnEnemyToBeSpawnedBase {
	private static final String CONFIG_NAME = "CreeperDestroyingWalls";
	private static final String CONFIG_COMMENT = "Creepers exploding when near the player behind the wall.";

	public AddAIToCreeperOnSpawn() {
		super( CONFIG_NAME, CONFIG_COMMENT, 1.0, GameState.State.NORMAL, false );
	}

	@Override
	public void onExecute( LivingEntity entity, ServerWorld world ) {
		CreeperEntity creeper = ( CreeperEntity )entity;

		creeper.goalSelector.addGoal( 1, new CreeperExplodeWallsGoal( creeper ) );
	}

	@Override
	public boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof CreeperEntity && super.shouldBeExecuted( entity );
	}
}
