package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.entities.PillagerWolfEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.world.server.ServerWorld;

/** Spawns Pillager in group with Pillager Wolves. */
public class SpawnPillagerGroup extends SpawnEnemyGroupBase {
	public SpawnPillagerGroup() {
		super( GameState.State.EXPERT, true, 2, 4, null );
	}

	@Override
	protected boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof PillagerEntity && super.shouldBeExecuted( entity );
	}

	@Override
	protected CreatureEntity spawnChild( ServerWorld world ) {
		return PillagerWolfEntity.type.create( world );
	}
}
