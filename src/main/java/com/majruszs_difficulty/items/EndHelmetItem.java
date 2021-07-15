package com.majruszs_difficulty.items;

import net.minecraft.block.Blocks;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

/** New late game helmet. */
public class EndHelmetItem extends EndArmorItem {
	public EndHelmetItem() {
		super( EquipmentSlotType.HEAD );
	}

	@Override
	public boolean isEnderMask( ItemStack stack, PlayerEntity player, EndermanEntity endermanEntity ) {
		return true;
	}
}
