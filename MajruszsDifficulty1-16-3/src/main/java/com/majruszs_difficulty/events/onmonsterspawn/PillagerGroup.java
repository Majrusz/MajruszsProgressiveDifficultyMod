package com.majruszs_difficulty.events.onmonsterspawn;

import com.majruszs_difficulty.entities.PillagerWolfEntity;
import com.majruszs_difficulty.goals.FollowGroupLeaderGoal;
import com.majruszs_difficulty.goals.TargetAsLeaderGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.world.server.ServerWorld;

public class PillagerGroup extends EnemyGroup {
	public PillagerGroup( MonsterEntity leader, ServerWorld world ) {
		super( leader, world, 2, 4 );
	}

	@Override
	protected LivingEntity spawnChild( ServerWorld world ) {
		PillagerWolfEntity wolf = new PillagerWolfEntity( PillagerWolfEntity.type, world );

		wolf.goalSelector.addGoal( 3, new FollowGroupLeaderGoal( wolf, leader, 1.0D, 6.0f, 5.0f ) );
		wolf.targetSelector.addGoal( 1, new TargetAsLeaderGoal( wolf, leader ) );

		return wolf;
	}
}

