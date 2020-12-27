package com.majruszs_difficulty.events.onmonsterspawn;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.server.ServerWorld;

public class SkeletonGroup extends EnemyGroup {
	protected static final double woodenSwordChance = 0.5D;
	protected static final double stoneSwordChance = 0.25D;

	public SkeletonGroup( MonsterEntity leader, ServerWorld world ) {
		super( leader, world, 1, 3 );

		this.leaderArmor = leatherArmor;
		giveArmorToLeader( leader, world, this.leaderArmor );
	}

	@Override
	protected LivingEntity spawnChild( ServerWorld world ) {
		SkeletonEntity skeleton = new SkeletonEntity( EntityType.SKELETON, world );

		double clampedRegionalDifficulty = MajruszsHelper.getClampedRegionalDifficulty( skeleton, world );

		ItemStack itemStack = generateWeapon();
		if( itemStack != null )
			skeleton.setItemStackToSlot( EquipmentSlotType.MAINHAND,
				MajruszsHelper.tryEnchantWeapon( MajruszsHelper.damageItem( itemStack ), clampedRegionalDifficulty )
			);

		setupGoals( skeleton, this.leader, 4, 0 );

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
