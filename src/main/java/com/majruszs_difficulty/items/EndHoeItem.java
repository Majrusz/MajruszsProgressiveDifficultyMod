package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import net.minecraft.item.HoeItem;
import net.minecraft.item.PickaxeItem;

/** New late game hoe. */
public class EndHoeItem extends HoeItem {
	public EndHoeItem() {
		super( CustomItemTier.END, -5, 0.0f, ( new Properties() ).group( Instances.ITEM_GROUP ).isImmuneToFire() );
	}
}
