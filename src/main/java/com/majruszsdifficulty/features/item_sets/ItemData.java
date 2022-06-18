package com.majruszsdifficulty.features.item_sets;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemData {
	public final Item item;
	public final EquipmentSlot[] equipmentSlots;

	public ItemData( Item item, EquipmentSlot... equipmentSlots ) {
		this.item = item;
		this.equipmentSlots = equipmentSlots;
	}

	public boolean hasItemEquipped( Player player ) {
		for( EquipmentSlot equipmentSlot : this.equipmentSlots )
			if( isValidItem( player.getItemBySlot( equipmentSlot ) ) )
				return true;

		return false;
	}

	public boolean isValidItem( ItemStack itemStack ) {
		return this.item.equals( itemStack.getItem() );
	}
}