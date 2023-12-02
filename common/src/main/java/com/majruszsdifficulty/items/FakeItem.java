package com.majruszsdifficulty.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/* Advancements require icons as items and this class is just a fake item to use these icons. */
public class FakeItem extends Item {
	public FakeItem() {
		super( new Properties().stacksTo( 1 ).rarity( Rarity.EPIC ) );
	}
}
