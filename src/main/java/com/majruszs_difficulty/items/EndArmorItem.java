package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;

/** New late game armor. */
public class EndArmorItem extends ArmorItem {
	public EndArmorItem( EquipmentSlotType slot ) {
		super( CustomArmorMaterial.END, slot, ( new Item.Properties() ).group( Instances.ITEM_GROUP ) );
	}
}
