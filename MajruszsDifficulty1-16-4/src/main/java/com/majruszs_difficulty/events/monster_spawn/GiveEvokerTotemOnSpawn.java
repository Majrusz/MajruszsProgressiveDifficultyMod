package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.ConfigHandlerOld.Config;
import com.majruszs_difficulty.GameState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/** Gives Totem for Evoker on spawn. */
public class GiveEvokerTotemOnSpawn extends GiveItemAfterSpawningBase {
	public GiveEvokerTotemOnSpawn() {
		super( GameState.State.NORMAL, false, EquipmentSlotType.MAINHAND, false, false );
	}

	@Override
	protected boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof EvokerEntity && super.shouldBeExecuted( entity );
	}

	@Override
	protected boolean isEnabled() {
		return !Config.isDisabled( Config.Features.EVOKER_TOTEM );
	}

	@Override
	protected double getChance() {
		return 1.0;
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack( Items.TOTEM_OF_UNDYING );
	}
}
