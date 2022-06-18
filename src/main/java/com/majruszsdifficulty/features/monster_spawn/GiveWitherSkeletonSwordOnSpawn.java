package com.majruszsdifficulty.features.monster_spawn;

import com.majruszsdifficulty.GameState;
import com.majruszsdifficulty.Instances;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.item.ItemStack;

/** Gives Wither Sword for Wither Skeleton on spawn. */
public class GiveWitherSkeletonSwordOnSpawn extends GiveItemAfterSpawningBase {
	private static final String CONFIG_NAME = "WitherSkeletonSword";
	private static final String CONFIG_COMMENT = "Wither Skeleton spawns with Wither Sword.";

	public GiveWitherSkeletonSwordOnSpawn() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.75, GameState.State.EXPERT, true, EquipmentSlot.MAINHAND, true, true );
	}

	@Override
	public boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof WitherSkeleton && super.shouldBeExecuted( entity );
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack( Instances.WITHER_SWORD );
	}
}
