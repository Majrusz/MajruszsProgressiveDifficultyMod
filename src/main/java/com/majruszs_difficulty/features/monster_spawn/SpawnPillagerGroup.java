package com.majruszs_difficulty.features.monster_spawn;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.entities.PillagerWolfEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Pillager;

/** Spawns Pillager in group with Pillager Wolves. */
public class SpawnPillagerGroup extends SpawnEnemyGroupBase {
	private static final String CONFIG_NAME = "PillagerGroup";
	private static final String CONFIG_COMMENT = "Pillager spawns in groups with Pillager's Wolves.";

	public SpawnPillagerGroup() {
		super( CONFIG_NAME, CONFIG_COMMENT, GameState.State.EXPERT, true, 2, 4, null );
	}

	@Override
	public boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof Pillager && super.shouldBeExecuted( entity );
	}

	@Override
	protected PathfinderMob spawnChild( ServerLevel world ) {
		return PillagerWolfEntity.type.create( world );
	}
}
