package com.majruszs_difficulty.events.onmonsterspawn;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.server.ServerWorld;

public class PiglinGroup extends EnemyGroup {
	protected static final double goldenSwordChance = 0.25D;

	public PiglinGroup( MonsterEntity leader, ServerWorld world ) {
		super( leader, world, 1, 3 );

		this.leaderArmor = goldenArmor;
		giveArmorToLeader( leader, world, this.leaderArmor );
	}

	@Override
	protected LivingEntity spawnChild( ServerWorld world ) {
		PiglinEntity piglin = new PiglinEntity( EntityType.field_233591_ai_, world );

		double clampedRegionalDifficulty = MajruszsHelper.getClampedRegionalDifficulty( piglin, world );

		ItemStack itemStack = generateWeapon();
		if( itemStack != null )
			piglin.setItemStackToSlot( EquipmentSlotType.MAINHAND,
				MajruszsHelper.tryEnchantWeapon( MajruszsHelper.damageItem( itemStack ), clampedRegionalDifficulty )
			);

		setupGoals( piglin, this.leader, 4, 0 );

		return piglin;
	}

	protected ItemStack generateWeapon() {
		double itemChance = MajruszsDifficulty.RANDOM.nextDouble();

		if( itemChance <= goldenSwordChance )
			return new ItemStack( Items.GOLDEN_SWORD );
		else
			return null;
	}
}
