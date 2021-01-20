package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.GameState;
import com.mlib.Random;
import com.mlib.config.DoubleConfig;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.server.ServerWorld;

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
	protected boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof PiglinEntity && super.shouldBeExecuted( entity );
	}

	@Override
	protected CreatureEntity spawnChild( ServerWorld world ) {
		return EntityType.PIGLIN.create( world );
	}

	@Override
	protected ItemStack generateWeaponForChild() {
		return Random.tryChance( goldenSwordChance.get() ) ? new ItemStack( Items.GOLDEN_SWORD ) : null;
	}
}
