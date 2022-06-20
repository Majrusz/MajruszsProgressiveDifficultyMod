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

	@Override
	public void fillItemCategory( CreativeModeTab itemGroup, NonNullList< ItemStack > itemStacks ) {
		if( itemGroup != Registries.ITEM_GROUP )
			return;

		itemStacks.add( UndeadArmorItem.constructItem( UndeadArmorItem.HELMET_ID ) );
		itemStacks.add( UndeadArmorItem.constructItem( UndeadArmorItem.CHESTPLATE_ID ) );
		itemStacks.add( UndeadArmorItem.constructItem( UndeadArmorItem.LEGGINGS_ID ) );
		itemStacks.add( UndeadArmorItem.constructItem( UndeadArmorItem.BOOTS_ID ) );
	}
}
