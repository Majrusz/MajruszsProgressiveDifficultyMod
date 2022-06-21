package com.majruszsdifficulty.features.monster_spawn;

import com.majruszsdifficulty.GameStage;
import com.mlib.MajruszLibrary;
import com.mlib.config.DoubleConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Spawns zombies in group. */
public class SpawnZombieGroup extends SpawnEnemyGroupBase {
	private static final String CONFIG_NAME = "ZombieGroup";
	private static final String CONFIG_COMMENT = "Zombies spawns in groups.";
	protected final DoubleConfig stoneSwordChance;
	protected final DoubleConfig woodenAxeChance;

	public SpawnZombieGroup() {
		super( CONFIG_NAME, CONFIG_COMMENT, GameStage.Stage.EXPERT, true, 1, 3, Armors.leather );

		String stone_comment = "Chance for followers to have a Stone Sword.";
		String wooden_comment = "Chance for followers to have a Wooden Axe.";
		this.stoneSwordChance = new DoubleConfig( "stone_sword_chance", stone_comment, false, 0.25, 0.0, 0.5 );
		this.woodenAxeChance = new DoubleConfig( "wooden_axe_chance", wooden_comment, false, 0.25, 0.0, 0.5 );
		this.featureGroup.addConfigs( this.woodenAxeChance, this.stoneSwordChance );
	}

	@Override
	public boolean shouldBeExecuted( LivingEntity entity ) {
		return !( entity instanceof ZombifiedPiglin ) && entity instanceof Zombie && notUndeadArmyMob( entity ) && super.shouldBeExecuted( entity );
	}

	@Override
	protected PathfinderMob spawnChild( ServerLevel world ) {
		return EntityType.ZOMBIE.create( world );
	}

	@Override
	protected ItemStack generateWeaponForChild() {
		double itemChance = MajruszLibrary.RANDOM.nextDouble();

		if( itemChance <= this.woodenAxeChance.get() )
			return new ItemStack( Items.WOODEN_AXE );
		else if( itemChance <= this.woodenAxeChance.get() + this.stoneSwordChance.get() )
			return new ItemStack( Items.STONE_SWORD );
		else
			return null;
	}
}
