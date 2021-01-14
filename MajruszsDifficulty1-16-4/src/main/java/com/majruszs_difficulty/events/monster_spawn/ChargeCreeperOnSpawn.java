package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.ConfigHandler.Config;
import com.majruszs_difficulty.GameState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.world.server.ServerWorld;

/** Charges creeper on spawn. (emulates attacking creeper with lightning bolt) */
public class ChargeCreeperOnSpawn extends OnEnemyToBeSpawnedBase {
	public ChargeCreeperOnSpawn() {
		super( GameState.State.NORMAL, true );
	}

	@Override
	protected boolean isEnabled() {
		return !Config.isDisabled( Config.Features.CREEPER_CHARGED );
	}

	@Override
	protected double getChance() {
		return Config.getChance( Config.Chances.CREEPER_CHARGED );
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
