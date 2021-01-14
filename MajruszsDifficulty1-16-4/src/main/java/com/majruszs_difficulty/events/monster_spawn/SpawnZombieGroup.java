package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.server.ServerWorld;

/** Spawns zombies in group. */
public class SpawnZombieGroup extends SpawnEnemyGroupBase {
	protected static final double stoneSwordChance = 0.25;
	protected static final double woodenAxeChance = 0.25;

	public SpawnZombieGroup() {
		super( GameState.State.EXPERT, true, 1, 3, Armors.leather );
	}

	@Override
	protected boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof ZombieEntity && super.shouldBeExecuted( entity );
	}

	@Override
	protected CreatureEntity spawnChild( ServerWorld world ) {
		return EntityType.ZOMBIE.create( world );
	}

	@Override
	protected ItemStack generateWeaponForChild() {
		double itemChance = MajruszsDifficulty.RANDOM.nextDouble();

		if( itemChance <= woodenAxeChance )
			return new ItemStack( Items.WOODEN_AXE );
		else if( itemChance <= woodenAxeChance + stoneSwordChance )
			return new ItemStack( Items.STONE_SWORD );
		else
			return null;
	}
}
