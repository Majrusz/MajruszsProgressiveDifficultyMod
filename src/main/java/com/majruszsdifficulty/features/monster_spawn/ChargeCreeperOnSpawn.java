package com.majruszsdifficulty.features.monster_spawn;

import com.majruszsdifficulty.GameState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;

/** Charges creeper on spawn. (emulates attacking creeper with lightning bolt) */
public class ChargeCreeperOnSpawn extends OnEnemyToBeSpawnedBase {
	private static final String CONFIG_NAME = "CreeperCharged";
	private static final String CONFIG_COMMENT = "Creepers spawning charged.";

	public ChargeCreeperOnSpawn() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.125, GameState.State.NORMAL, true );
	}

	@Override
	public void onExecute( LivingEntity entity, ServerLevel world ) {
		Creeper creeper = ( Creeper )entity;

		LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create( world );
		if( lightningBolt != null )
			creeper.thunderHit( world, lightningBolt );

		creeper.clearFire();
	}

	@Override
	public boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof Creeper && super.shouldBeExecuted( entity );
	}
}
