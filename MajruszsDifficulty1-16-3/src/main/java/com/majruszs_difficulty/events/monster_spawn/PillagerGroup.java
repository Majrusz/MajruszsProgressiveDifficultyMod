package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.entities.PillagerWolfEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.world.server.ServerWorld;

public class PillagerGroup extends EnemyGroup {
	public PillagerGroup( CreatureEntity leader, ServerWorld world ) {
		super( leader, world, 2, 4 );
	}

	@Override
	protected CreatureEntity spawnChild( ServerWorld world ) {
		PillagerWolfEntity wolf = PillagerWolfEntity.type.create( world );

		setupGoals( wolf, 9, 9 );

		return wolf;
	}
}

