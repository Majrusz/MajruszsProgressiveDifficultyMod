package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/** Wither sword. */
public class WitherSwordItem extends SwordItem {
	private final static String TOOLTIP_TRANSLATION_KEY = "item.majruszs_difficulty.wither_sword.tooltip";

	public WitherSwordItem() {
		super( CustomItemTier.WITHER, 3, -2.4f, ( new Item.Properties() ).tab( Instances.ITEM_GROUP )
			.rarity( Rarity.UNCOMMON ) );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		MajruszsHelper.addAdvancedTranslatableText( tooltip, flag, TOOLTIP_TRANSLATION_KEY );
	}
}
