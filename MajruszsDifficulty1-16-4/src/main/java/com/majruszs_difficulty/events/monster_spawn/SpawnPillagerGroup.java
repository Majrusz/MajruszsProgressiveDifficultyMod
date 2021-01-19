package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.entities.PillagerWolfEntity;
import com.mlib.config.DoubleConfig;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.world.server.ServerWorld;

/** Spawns Pillager in group with Pillager Wolves. */
public class SpawnPillagerGroup extends SpawnEnemyGroupBase {
	private static final String CONFIG_NAME = "PillagerGroup";
	private static final String CONFIG_COMMENT = "Pillager spawns in groups with Pillager's Wolves.";

	public SpawnPillagerGroup() {
		super( CONFIG_NAME, CONFIG_COMMENT, GameState.State.EXPERT, true, 2, 4, null );
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
