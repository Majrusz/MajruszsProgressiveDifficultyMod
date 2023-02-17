package com.majruszsdifficulty.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class SoulJarItem extends Item {
	public SoulJarItem() {
		super( new Properties().stacksTo( 1 ).rarity( Rarity.UNCOMMON ) );
	}
}
