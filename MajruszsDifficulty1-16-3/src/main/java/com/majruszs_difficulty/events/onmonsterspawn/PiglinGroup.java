package com.majruszs_difficulty.events.onmonsterspawn;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.server.ServerWorld;

public class PiglinGroup extends EnemyGroup {
	protected static final double goldenSwordChance = 0.25D;

	public PiglinGroup( CreatureEntity leader, ServerWorld world ) {
		super( leader, world, 1, 3 );

		giveArmorToLeader( world, Armors.golden );
	}

	@Override
	protected CreatureEntity spawnChild( ServerWorld world ) {
		PiglinEntity piglin = new PiglinEntity( EntityType.field_233591_ai_, world );

		giveWeaponTo( piglin, world );
		setupGoals( piglin, 9, 9 );

		return piglin;
	}

	@Override
	protected ItemStack generateWeapon() {
		double itemChance = MajruszsDifficulty.RANDOM.nextDouble();

		if( itemChance <= goldenSwordChance )
			return new ItemStack( Items.GOLDEN_SWORD );
		else
			return null;
	}
}
