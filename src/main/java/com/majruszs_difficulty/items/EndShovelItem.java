package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;

/** New late game shovel. */
public class EndShovelItem extends ShovelItem {
	public EndShovelItem() {
		super( CustomItemTier.END, 1.5f, -3.0f, ( new Properties() ).group( Instances.ITEM_GROUP ).isImmuneToFire() );
	}
}
