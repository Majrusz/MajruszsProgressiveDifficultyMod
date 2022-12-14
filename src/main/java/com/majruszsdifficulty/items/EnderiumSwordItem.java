package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;

public class EnderiumSwordItem extends SwordItem {
	public EnderiumSwordItem() {
		super( CustomItemTier.END, 4, -2.6f, new Properties().rarity( Rarity.UNCOMMON ).fireResistant() );
	}
}
