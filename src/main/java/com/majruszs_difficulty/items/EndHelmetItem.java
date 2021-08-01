package com.majruszs_difficulty.items;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** New late game helmet. */
public class EndHelmetItem extends EndArmorItem {
	public EndHelmetItem() {
		super( EquipmentSlot.HEAD );
	}

	@Override
	public boolean isEnderMask( ItemStack stack, Player player, EnderMan endermanEntity ) {
		return true;
	}
}
