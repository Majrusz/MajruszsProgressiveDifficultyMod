package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;

/** New late game sword. */
public class EndSwordItem extends SwordItem {
	public EndSwordItem() {
		super( CustomItemTier.END, 3, -2.4f, ( new Item.Properties() ).group( Instances.ITEM_GROUP )
			.isImmuneToFire() );
	}
}
