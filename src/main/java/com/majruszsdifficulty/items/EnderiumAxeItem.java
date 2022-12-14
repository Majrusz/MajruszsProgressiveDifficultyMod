package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Rarity;

public class EnderiumAxeItem extends AxeItem {
	public EnderiumAxeItem() {
		super( CustomItemTier.END, 6.0f, -3.1f, new Properties().rarity( Rarity.UNCOMMON ).fireResistant() );
	}
}