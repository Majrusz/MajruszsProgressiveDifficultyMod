package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

/** Advancements require icons as items and this class is just a fake item to use these icons. */
public class FakeItem extends Item {
	public FakeItem() {
		super( new Properties().stacksTo( 1 ).tab( Registries.ITEM_GROUP ).rarity( Rarity.EPIC ) );
	}

	@Override
	public void fillItemCategory( CreativeModeTab tab, NonNullList< ItemStack > itemStacks ) {}
}
