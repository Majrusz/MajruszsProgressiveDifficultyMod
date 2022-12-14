package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;

public class EnderiumShovelItem extends ShovelItem {
	public EnderiumShovelItem() {
		super( CustomItemTier.END, 1.5f, -3.0f, new Properties().rarity( Rarity.UNCOMMON ).fireResistant() );
	}
}
