package com.majruszs_difficulty.events.onmonsterspawn;

import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
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
		PiglinEntity piglin = EntityType.field_233591_ai_.create( world );

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
