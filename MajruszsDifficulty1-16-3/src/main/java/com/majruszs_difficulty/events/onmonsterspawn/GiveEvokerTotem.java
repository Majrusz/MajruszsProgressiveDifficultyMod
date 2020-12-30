package com.majruszs_difficulty.events.onmonsterspawn;

import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class GiveEvokerTotem {
	public static void giveTo( EvokerEntity evoker ) {
		evoker.setHeldItem( Hand.MAIN_HAND, new ItemStack( Items.TOTEM_OF_UNDYING ) );
	}
}
