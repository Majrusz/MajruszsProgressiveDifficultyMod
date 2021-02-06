package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.SwordItem;

/** New late game pickaxe. */
public class EndPickaxeItem extends PickaxeItem {
	public EndPickaxeItem() {
		super( CustomItemTier.END, 1, -2.8f, ( new Properties() ).group( Instances.ITEM_GROUP ).isImmuneToFire() );
	}
}
