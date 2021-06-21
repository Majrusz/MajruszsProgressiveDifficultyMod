package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.NonNullList;

/** Advancements require icons as items, so this class is just a fake item to have a custom icons. */
public class FakeItem extends Item {
	public FakeItem() {
		super( ( new Properties() ).maxStackSize( 1 )
			.group( Instances.ITEM_GROUP ).rarity( Rarity.EPIC ) );
	}

	@Override
	public void fillItemGroup( ItemGroup itemGroup, NonNullList< ItemStack > itemStacks ) {}
}
