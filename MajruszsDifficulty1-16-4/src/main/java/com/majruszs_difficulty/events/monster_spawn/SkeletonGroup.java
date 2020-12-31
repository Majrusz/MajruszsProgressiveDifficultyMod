package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.server.ServerWorld;

public class SkeletonGroup extends EnemyGroup {
	protected static final double woodenSwordChance = 0.5D;
	protected static final double stoneSwordChance = 0.25D;

	public SkeletonGroup( MonsterEntity leader, ServerWorld world ) {
		super( leader, world, 1, 3 );

		giveArmorToLeader( world, Armors.leather );
	}

	@Override
	protected CreatureEntity spawnChild( ServerWorld world ) {
		SkeletonEntity skeleton = EntityType.SKELETON.create( world );

		giveWeaponTo( skeleton, world );
		setupGoals( skeleton, 9, 9 );

		return skeleton;
	}

	protected ItemStack generateWeapon() {
		double itemChance = MajruszsDifficulty.RANDOM.nextDouble();

		if( itemChance <= woodenSwordChance )
			return new ItemStack( Items.WOODEN_SWORD );
		else if( itemChance <= woodenSwordChance + stoneSwordChance )
			return new ItemStack( Items.STONE_SWORD );
		else
			return null;
	}
}
