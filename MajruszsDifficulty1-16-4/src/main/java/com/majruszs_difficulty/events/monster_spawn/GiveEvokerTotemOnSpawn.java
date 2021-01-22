package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.GameState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/** Gives Totem for Evoker on spawn. */
public class GiveEvokerTotemOnSpawn extends GiveItemAfterSpawningBase {
	private static final String CONFIG_NAME = "EvokerTotem";
	private static final String CONFIG_COMMENT = "Evoker spawns with Totem of Undying.";

	public GiveEvokerTotemOnSpawn() {
		super( CONFIG_NAME, CONFIG_COMMENT, 1.0, GameState.State.NORMAL, false, EquipmentSlotType.MAINHAND, false, false );
	}

	@Override
	protected boolean shouldBeExecuted( LivingEntity entity ) {
		return entity instanceof EvokerEntity && super.shouldBeExecuted( entity );
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack( Items.TOTEM_OF_UNDYING );
	}
}
