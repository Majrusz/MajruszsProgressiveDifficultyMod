package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;

/** New strong end-game ingot. */
public class EndIngotItem extends Item {
	public EndIngotItem() {
		super( new Properties().group( Instances.ITEM_GROUP )
			.rarity( Rarity.UNCOMMON ) );
	}
}
