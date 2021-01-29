package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;

/** End Crystal which is drop from End Crystal Ore. */
public class EndCrystalItem extends Item {
	public EndCrystalItem() {
		super( new Item.Properties().group( Instances.ITEM_GROUP ).rarity( Rarity.UNCOMMON ) );
	}
}
