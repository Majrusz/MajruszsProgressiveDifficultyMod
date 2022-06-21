package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Cloth required to make Undead Army Battle Standard. */
public class ClothItem extends Item {
	public ClothItem() {
		super( new Properties().tab( Registries.ITEM_GROUP ) );
	}
}
