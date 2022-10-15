package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Rarity;

public class EnderiumHoeItem extends HoeItem {
	public EnderiumHoeItem() {
		super( CustomItemTier.END, -5, 0.0f, new Properties().tab( Registries.ITEM_GROUP ).rarity( Rarity.UNCOMMON ).fireResistant() );
	}
}
