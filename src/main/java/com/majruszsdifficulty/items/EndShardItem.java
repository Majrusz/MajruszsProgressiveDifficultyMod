package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/** End Crystal which is drop from End Crystal Ore. */
public class EndShardItem extends Item {
	public EndShardItem() {
		super( new Item.Properties().tab( Registries.ITEM_GROUP ).rarity( Rarity.UNCOMMON ) );
	}
}
