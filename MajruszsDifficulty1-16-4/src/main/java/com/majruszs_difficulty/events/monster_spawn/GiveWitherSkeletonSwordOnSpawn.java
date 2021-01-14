package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.ConfigHandler.Config;
import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.RegistryHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

/** Gives Wither Sword for Wither Skeleton on spawn. */
public class GiveWitherSkeletonSwordOnSpawn extends GiveItemAfterSpawningBase {
	public GiveWitherSkeletonSwordOnSpawn() {
		super( GameState.State.EXPERT, true, EquipmentSlotType.MAINHAND, true, true );
	}

	@Override
	protected boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof WitherSkeletonEntity && super.shouldBeExecuted( entity );
	}

	@Override
	protected boolean isEnabled() {
		return !Config.isDisabled( Config.Features.WITHER_SKELETON_SWORD );
	}

	@Override
	protected double getChance() {
		return Config.getChance( Config.Chances.WITHER_SKELETON_SWORD );
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack( RegistryHandler.WITHER_SWORD.get() );
	}
}
