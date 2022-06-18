package com.majruszsdifficulty.features.monster_spawn;

import com.majruszsdifficulty.GameState;
import com.majruszsdifficulty.entities.ParasiteEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;

/** Spawns extra skeleton and makes him ride the spider. */
public class CreateJockeyOnSpiderSpawn extends OnEnemyToBeSpawnedBase {
	private static final String CONFIG_NAME = "Jockey";
	private static final String CONFIG_COMMENT = "Extra chance for jockey to spawn.";

	public CreateJockeyOnSpiderSpawn() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.125, GameState.State.EXPERT, false );
	}

	@Override
	public void onExecute( LivingEntity entity, ServerLevel world ) {
		Skeleton skeleton = EntityType.SKELETON.create( world );
		if( skeleton == null )
			return;

		skeleton.moveTo( entity.getX(), entity.getY(), entity.getZ(), entity.yBodyRot, 0.0f );
		skeleton.finalizeSpawn( world, world.getCurrentDifficultyAt( entity.blockPosition() ), MobSpawnType.JOCKEY, null, null );
		skeleton.startRiding( entity );
	}

	@Override
	public boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof Spider && !( entity instanceof CaveSpider ) && !( entity instanceof ParasiteEntity ) && super.shouldBeExecuted(
			entity );
	}
}
