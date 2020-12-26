package com.majruszs_difficulty.events.onmonsterspawn;

import com.majruszs_difficulty.RegistryHandler;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class GiveWitherSkeletonSword {
	public static void giveTo( WitherSkeletonEntity witherSkeleton ) {
		witherSkeleton.setHeldItem( Hand.MAIN_HAND, new ItemStack( RegistryHandler.WITHER_SWORD.get() ) );
	}
}
