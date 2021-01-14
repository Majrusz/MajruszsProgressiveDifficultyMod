package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.server.ServerWorld;

/** Spawns skeletons in group. */
public class SpawnSkeletonGroup extends SpawnEnemyGroupBase {
	protected static final double woodenSwordChance = 0.5;
	protected static final double stoneSwordChance = 0.25;

	public SpawnSkeletonGroup() {
		super( GameState.State.EXPERT, true, 1, 3, Armors.leather );
	}

	@Override
	protected boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof SkeletonEntity && super.shouldBeExecuted( entity );
	}

	@Override
	protected CreatureEntity spawnChild( ServerWorld world ) {
		return EntityType.SKELETON.create( world );
	}

	@Override
	protected ItemStack generateWeaponForChild() {
		double itemChance = MajruszsDifficulty.RANDOM.nextDouble();

		if( itemChance <= woodenSwordChance )
			return new ItemStack( Items.WOODEN_SWORD );
		else if( itemChance <= woodenSwordChance + stoneSwordChance )
			return new ItemStack( Items.STONE_SWORD );
		else
			return null;
	}
}
