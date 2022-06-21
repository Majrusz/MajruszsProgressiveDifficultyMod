package com.majruszsdifficulty.features.monster_spawn;

import com.majruszsdifficulty.GameStage;
import com.mlib.MajruszLibrary;
import com.mlib.config.DoubleConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Spawns skeletons in group. */
public class SpawnSkeletonGroup extends SpawnEnemyGroupBase {
	private static final String CONFIG_NAME = "SkeletonGroup";
	private static final String CONFIG_COMMENT = "Skeleton spawns in groups.";
	protected final DoubleConfig woodenSwordChance;
	protected final DoubleConfig stoneSwordChance;

	public SpawnSkeletonGroup() {
		super( CONFIG_NAME, CONFIG_COMMENT, GameStage.Stage.EXPERT, true, 1, 3, Armors.leather );

		String wooden_comment = "Chance for followers to have a Wooden Sword.";
		String stone_comment = "Chance for followers to have a Stone Sword.";
		this.woodenSwordChance = new DoubleConfig( "wooden_sword_chance", wooden_comment, false, 0.5, 0.0, 0.5 );
		this.stoneSwordChance = new DoubleConfig( "stone_sword_chance", stone_comment, false, 0.25, 0.0, 0.5 );
		this.featureGroup.addConfigs( this.woodenSwordChance, this.stoneSwordChance );
	}

	@Override
	public boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof Skeleton && notUndeadArmyMob( entity ) && super.shouldBeExecuted( entity );
	}

	@Override
	protected PathfinderMob spawnChild( ServerLevel world ) {
		return EntityType.SKELETON.create( world );
	}

	@Override
	protected ItemStack generateWeaponForChild() {
		double itemChance = MajruszLibrary.RANDOM.nextDouble();

		if( itemChance <= this.woodenSwordChance.get() )
			return new ItemStack( Items.WOODEN_SWORD );
		else if( itemChance <= this.woodenSwordChance.get() + this.stoneSwordChance.get() )
			return new ItemStack( Items.STONE_SWORD );
		else
			return null;
	}
}
