package com.majruszs_difficulty.features.monster_spawn;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.entities.EliteSkeletonEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Spawns Elite Skeletons in group. */
public class SpawnEliteSkeletonGroup extends SpawnEnemyGroupBase {
	private static final String CONFIG_NAME = "EliteSkeletonGroup";
	private static final String CONFIG_COMMENT = "Elite Skeleton spawns in groups.";

	public SpawnEliteSkeletonGroup() {
		super( CONFIG_NAME, CONFIG_COMMENT, GameState.State.MASTER, true, 4, 6, Armors.golden );
	}

	@Override
	public boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof EliteSkeletonEntity && notUndeadArmyMob( entity ) && super.shouldBeExecuted( entity );
	}

	@Override
	protected PathfinderMob spawnChild( ServerLevel world ) {
		return EliteSkeletonEntity.type.create( world );
	}

	@Override
	protected ItemStack generateWeaponForChild() {
		return new ItemStack( Items.GOLDEN_SWORD );
	}
}
