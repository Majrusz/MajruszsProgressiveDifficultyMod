package com.majruszs_difficulty.events.onmonsterspawn;

import com.majruszs_difficulty.entities.PillagerWolfEntity;
import com.majruszs_difficulty.goals.FollowGroupLeaderGoal;
import com.majruszs_difficulty.goals.TargetAsLeaderGoal;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;

public class PillagerGroup extends EnemyGroup {
	public PillagerGroup( CreatureEntity leader, ServerWorld world ) {
		super( leader, world, 2, 4 );
	}

	@Override
	protected CreatureEntity spawnChild( ServerWorld world ) {
		PillagerWolfEntity wolf = new PillagerWolfEntity( PillagerWolfEntity.type, world );

		setupGoals( wolf,9, 9 );

		return wolf;
	}
}

