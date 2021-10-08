package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

/** Advancements require icons as items, so this class is just a fake item to have a custom icons. */
public class FakeItem extends Item {
	public FakeItem() {
		super( ( new Properties() ).stacksTo( 1 ).tab( Instances.ITEM_GROUP ).rarity( Rarity.EPIC ) );
	}

	@Override
	public void fillItemCategory( CreativeModeTab tab, NonNullList< ItemStack > itemStacks ) {}
}
