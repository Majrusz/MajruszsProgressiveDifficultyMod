package com.majruszs_difficulty.features.end_items;

import com.majruszs_difficulty.items.*;
import net.minecraft.item.Item;

/** Class with common functions/variables for end items special functionalities. */
public class EndItems {
	public static class Keys {
		public static final String HASTE_TOOLTIP = "majruszs_difficulty.end_items.haste_tooltip";
		public static final String BLEED_TOOLTIP = "majruszs_difficulty.end_items.bleed_tooltip";
		public static final String LEVITATION_TOOLTIP = "majruszs_difficulty.end_items.levitation_tooltip";
	}

	/** Returns whether item is either end tool or end sword. */
	public static boolean isEndItem( Item item ) {
		return item instanceof EndHoeItem || item instanceof EndAxeItem || item instanceof EndPickaxeItem || item instanceof EndShovelItem || item instanceof EndSwordItem;
	}

	/** Returns whether item is should have increased chance for inflicting Bleeding effect. */
	public static boolean haveExtraBleedingChance( Item item ) {
		return item instanceof EndHoeItem || item instanceof EndAxeItem || item instanceof EndSwordItem;
	}

	/** Returns whether item can inflicts Levitation effect. */
	public static boolean canInflictLevitation( Item item ) {
		return item instanceof EndHoeItem || item instanceof EndPickaxeItem || item instanceof EndShovelItem;
	}
}
