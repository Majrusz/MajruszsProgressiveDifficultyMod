package com.majruszsdifficulty.features.monster_spawn;

import com.majruszsdifficulty.GameState;
import com.mlib.Random;
import com.mlib.config.DoubleConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Spawns piglins in group. */
public class SpawnPiglinGroup extends SpawnEnemyGroupBase {
	private static final String CONFIG_NAME = "PiglingGroup";
	private static final String CONFIG_COMMENT = "Piglin spawns in groups.";
	protected final DoubleConfig goldenSwordChance;

	public SpawnPiglinGroup() {
		super( CONFIG_NAME, CONFIG_COMMENT, GameState.State.EXPERT, true, 1, 3, Armors.golden );

		String comment = "Chance for followers to have a Golden Sword.";
		this.goldenSwordChance = new DoubleConfig( "golden_sword_chance", comment, false, 0.25, 0.0, 0.5 );
		this.featureGroup.addConfig( this.goldenSwordChance );
	}

	@Override
	public boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof Piglin && super.shouldBeExecuted( entity );
	}

	@Override
	protected PathfinderMob spawnChild( ServerLevel world ) {
		return EntityType.PIGLIN.create( world );
	}

	@Override
	protected ItemStack generateWeaponForChild() {
		return Random.tryChance( goldenSwordChance.get() ) ? new ItemStack( Items.GOLDEN_SWORD ) : null;
	}
}
