package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import net.minecraft.item.AxeItem;

/** New late game axe. */
public class EndAxeItem extends AxeItem {
	public EndAxeItem() {
		super( CustomItemTier.END, 6.0f, -3.1f, ( new Properties() ).group( Instances.ITEM_GROUP )
			.isImmuneToFire() );
	}
}
