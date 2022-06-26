package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import net.minecraft.world.item.*;

public class EnderiumSwordItem extends SwordItem {
	public EnderiumSwordItem() {
		super( CustomItemTier.END, 4, -2.6f, new Properties().tab( Registries.ITEM_GROUP ).rarity( Rarity.UNCOMMON ).fireResistant() );
	}
}
