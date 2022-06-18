package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/** New strong end-game ingot. */
public class EndIngotItem extends Item {
	public EndIngotItem() {
		super( new Properties().tab( Registries.ITEM_GROUP ).rarity( Rarity.UNCOMMON ) );
	}
}
