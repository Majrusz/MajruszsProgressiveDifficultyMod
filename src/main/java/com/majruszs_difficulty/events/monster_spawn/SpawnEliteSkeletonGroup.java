package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.entities.EliteSkeletonEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.server.ServerWorld;

/** Spawns Elite Skeletons in group. */
public class SpawnEliteSkeletonGroup extends SpawnEnemyGroupBase {
	private static final String CONFIG_NAME = "EliteSkeletonGroup";
	private static final String CONFIG_COMMENT = "Elite Skeleton spawns in groups.";

	public SpawnEliteSkeletonGroup() {
		super( CONFIG_NAME, CONFIG_COMMENT, GameState.State.MASTER, true, 4, 6, Armors.golden );
	}

	@Override
	public boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof EliteSkeletonEntity && super.shouldBeExecuted( entity );
	}

	@Override
	protected CreatureEntity spawnChild( ServerWorld world ) {
		return EliteSkeletonEntity.type.create( world );
	}

	@Override
	protected ItemStack generateWeaponForChild() {
		return new ItemStack( Items.GOLDEN_SWORD );
	}
}
