package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;

public class EnderiumPickaxeItem extends PickaxeItem {
	public EnderiumPickaxeItem() {
		super( CustomItemTier.END, 1, -2.8f, new Properties().tab( Registries.ITEM_GROUP ).rarity( Rarity.UNCOMMON ).fireResistant() );
	}
}
