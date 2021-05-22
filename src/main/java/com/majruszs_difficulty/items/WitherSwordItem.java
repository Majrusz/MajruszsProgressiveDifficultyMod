package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.SwordItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/** Wither sword. */
public class WitherSwordItem extends SwordItem {
	private final static String TOOLTIP_TRANSLATION_KEY = "item.majruszs_difficulty.wither_sword.tooltip";
	public WitherSwordItem() {
		super( CustomItemTier.WITHER, 3, -2.4f, ( new Item.Properties() ).group( Instances.ITEM_GROUP )
			.rarity( Rarity.UNCOMMON ) );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack itemStack, @Nullable World world, List< ITextComponent > tooltip, ITooltipFlag flag ) {
		MajruszsHelper.addAdvancedTooltip( tooltip, flag, TOOLTIP_TRANSLATION_KEY );
	}
}
