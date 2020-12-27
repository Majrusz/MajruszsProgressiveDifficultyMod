package com.majruszs_difficulty.events.onmonsterspawn;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.server.ServerWorld;

public class ZombieGroup extends EnemyGroup {
	protected static final double stoneSwordChance = 0.25D;
	protected static final double woodenAxeChance = 0.25D;

	public ZombieGroup( MonsterEntity leader, ServerWorld world ) {
		super( leader, world, 1, 3 );

		this.leaderArmor = leatherArmor;
		giveArmorToLeader( leader, world, this.leaderArmor );
	}

	@Override
	protected LivingEntity spawnChild( ServerWorld world ) {
		ZombieEntity zombie = new ZombieEntity( EntityType.ZOMBIE, world );

		double clampedRegionalDifficulty = MajruszsHelper.getClampedRegionalDifficulty( zombie, world );

		ItemStack itemStack = generateWeapon();
		if( itemStack != null ) {
			zombie.setItemStackToSlot( EquipmentSlotType.MAINHAND, MajruszsHelper.tryEnchantWeapon( MajruszsHelper.damageItem( itemStack ), clampedRegionalDifficulty ) );
		}

		setupGoals( zombie, this.leader, 4, 0 );

		return zombie;
	}

	protected ItemStack generateWeapon() {
		double itemChance = MajruszsDifficulty.RANDOM.nextDouble();

		if( itemChance <= woodenAxeChance )
			return new ItemStack( Items.WOODEN_AXE );
		else if( itemChance <= woodenAxeChance + stoneSwordChance )
			return new ItemStack( Items.STONE_SWORD );
		else
			return null;
	}
}
