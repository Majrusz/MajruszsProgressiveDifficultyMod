package com.majruszsdifficulty.items;

import net.minecraft.world.entity.EquipmentSlot;

/** New late game helmet. */
public class EndHelmetItem extends EndArmorItem {
	public EndHelmetItem() {
		super( EquipmentSlot.HEAD );
	}

	/*@Override
	public boolean isEnderMask( ItemStack stack, Player player, EnderMan endermanEntity ) {
		return Registries.ItemSets.END.countSetItems( player ) >= 2;
	}*/
}
