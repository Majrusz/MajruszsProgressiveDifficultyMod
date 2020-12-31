package com.majruszs_difficulty.events.monster_spawn;

import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.RegistryHandler;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;

public class GiveWitherSkeletonSword {
	public static void giveTo( WitherSkeletonEntity witherSkeleton ) {
		if( !witherSkeleton.isServerWorld() )
			return;

		double clampedRegionalDifficulty = MajruszsHelper.getClampedRegionalDifficulty( witherSkeleton, ( ServerWorld )witherSkeleton.world );
		ItemStack sword = new ItemStack( RegistryHandler.WITHER_SWORD.get() );

		witherSkeleton.setHeldItem( Hand.MAIN_HAND, MajruszsHelper.damageAndEnchantItem( sword, clampedRegionalDifficulty ) );
	}
}
