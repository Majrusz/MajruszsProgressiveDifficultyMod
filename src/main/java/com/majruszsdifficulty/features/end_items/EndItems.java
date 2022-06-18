package com.majruszsdifficulty.features.end_items;

import com.majruszsdifficulty.items.*;
import com.mlib.features.FarmlandTiller;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;

/** Class with common functions/variables for end items special functionalities. */
@Mod.EventBusSubscriber
public class EndItems {
	static {
		FarmlandTiller.registerList.add( new FarmlandTiller.Register() {
			@Override
			public boolean shouldBeExecuted( ServerLevel serverLevel, Player player, ItemStack itemStack ) {
				return itemStack.getItem() instanceof HoeItem;
			}
		} );
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

	public static class Keys {
		public static final String HASTE_TOOLTIP = "majruszsdifficulty.end_items.haste_tooltip";
		public static final String BLEED_TOOLTIP = "majruszsdifficulty.end_items.bleed_tooltip";
		public static final String LEVITATION_TOOLTIP = "majruszsdifficulty.end_items.levitation_tooltip";
		public static final String TILL_TOOLTIP = "majruszsdifficulty.end_items.till_tooltip";
	}
}
