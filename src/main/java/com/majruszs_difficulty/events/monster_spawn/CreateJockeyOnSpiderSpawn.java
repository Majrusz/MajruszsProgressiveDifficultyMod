package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.GameState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.world.server.ServerWorld;

/** Spawns extra skeleton and makes him ride the spider. */
public class CreateJockeyOnSpiderSpawn extends OnEnemyToBeSpawnedBase {
	private static final String CONFIG_NAME = "Jockey";
	private static final String CONFIG_COMMENT = "Extra chance for jockey to spawn.";

	public CreateJockeyOnSpiderSpawn() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.25, GameState.State.EXPERT, false );
	}

	@Override
	public void onExecute( LivingEntity entity, ServerWorld world ) {
		SkeletonEntity skeleton = EntityType.SKELETON.create( world );
		if( skeleton == null )
			return;

		skeleton.setLocationAndAngles( entity.getPosX(), entity.getPosY(), entity.getPosZ(), entity.rotationYaw, 0.0f );
		skeleton.onInitialSpawn( world, world.getDifficultyForLocation( entity.getPosition() ), SpawnReason.JOCKEY, null, null );
		skeleton.startRiding( entity );
	}

	@Override
	protected boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof SpiderEntity && !( entity instanceof CaveSpiderEntity ) && super.shouldBeExecuted( entity );
	}
}
