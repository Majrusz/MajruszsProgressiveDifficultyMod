package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.mlib.config.DoubleConfig;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.server.ServerWorld;

/** Spawns zombies in group. */
public class SpawnZombieGroup extends SpawnEnemyGroupBase {
	private static final String CONFIG_NAME = "ZombieGroup";
	private static final String CONFIG_COMMENT = "Zombies spawns in groups.";
	protected final DoubleConfig stoneSwordChance;
	protected final DoubleConfig woodenAxeChance;

	public SpawnZombieGroup() {
		super( CONFIG_NAME, CONFIG_COMMENT, GameState.State.EXPERT, true, 1, 3, Armors.leather );

		String stone_comment = "Chance for followers to have a Stone Sword.";
		String wooden_comment = "Chance for followers to have a Wooden Axe.";
		this.stoneSwordChance = new DoubleConfig( "stone_sword_chance", stone_comment, false, 0.25, 0.0, 0.5 );
		this.woodenAxeChance = new DoubleConfig( "wooden_axe_chance", wooden_comment, false, 0.25, 0.0, 0.5 );
		this.featureGroup.addConfigs( this.woodenAxeChance, this.stoneSwordChance );
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

		if( itemChance <= this.woodenAxeChance.get() )
			return new ItemStack( Items.WOODEN_AXE );
		else if( itemChance <= this.woodenAxeChance.get() + this.stoneSwordChance.get() )
			return new ItemStack( Items.STONE_SWORD );
		else
			return null;
	}
}
