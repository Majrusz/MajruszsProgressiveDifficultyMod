package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.server.ServerWorld;

/** Spawns piglins in group. */
public class SpawnPiglinGroup extends SpawnEnemyGroupBase {
	protected static final double goldenSwordChance = 0.25;

	public SpawnPiglinGroup() {
		super( GameState.State.EXPERT, true, 1, 3, Armors.golden );
	}

	@Override
	protected boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof PiglinEntity && super.shouldBeExecuted( entity );
	}

	@Override
	protected CreatureEntity spawnChild( ServerWorld world ) {
		return EntityType.PIGLIN.create( world );
	}

	@Override
	protected ItemStack generateWeaponForChild() {
		return MajruszsHelper.tryChance( goldenSwordChance ) ? new ItemStack( Items.GOLDEN_SWORD ) : null;
	}
}
