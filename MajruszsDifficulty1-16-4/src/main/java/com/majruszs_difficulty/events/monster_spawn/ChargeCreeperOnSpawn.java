package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.GameState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.world.server.ServerWorld;

/** Charges creeper on spawn. (emulates attacking creeper with lightning bolt) */
public class ChargeCreeperOnSpawn extends OnEnemyToBeSpawnedBase {
	private static final String CONFIG_NAME = "CreeperCharged";
	private static final String CONFIG_COMMENT = "Creepers spawning charged.";

	public ChargeCreeperOnSpawn() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.125, GameState.State.NORMAL, true );
	}

	@Override
	public void onExecute( LivingEntity entity, ServerWorld world ) {
		CreeperEntity creeper = ( CreeperEntity )entity;

		LightningBoltEntity lightningBolt = EntityType.LIGHTNING_BOLT.create( world );
		if( lightningBolt != null )
			creeper.func_241841_a( world, lightningBolt );

		creeper.extinguish();
	}

	@Override
	protected boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof CreeperEntity && super.shouldBeExecuted( entity );
	}
}
