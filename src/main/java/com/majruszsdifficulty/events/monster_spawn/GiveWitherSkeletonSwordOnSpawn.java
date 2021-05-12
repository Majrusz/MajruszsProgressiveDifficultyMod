package com.majruszsdifficulty.events.monster_spawn;

import com.majruszsdifficulty.GameState;
import com.majruszsdifficulty.Instances;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

/** Gives Wither Sword for Wither Skeleton on spawn. */
public class GiveWitherSkeletonSwordOnSpawn extends GiveItemAfterSpawningBase {
	private static final String CONFIG_NAME = "WitherSkeletonSword";
	private static final String CONFIG_COMMENT = "Wither Skeleton spawns with Wither Sword.";

	public GiveWitherSkeletonSwordOnSpawn() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.75, GameState.State.EXPERT, true, EquipmentSlotType.MAINHAND, true, true );
	}

	@Override
	protected boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof WitherSkeletonEntity && super.shouldBeExecuted( entity );
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack( Instances.Tools.WITHER_SWORD );
	}
}
