package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.SwordItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/** New late game sword. */
public class EndSwordItem extends SwordItem {
	private static final String TOOLTIP_TRANSLATION_KEY = "item.majruszs_difficulty.end_sword.item_tooltip";

	public EndSwordItem() {
		super( CustomItemTier.END, 3, -2.4f, ( new Item.Properties() ).group( Instances.ITEM_GROUP ).rarity( Rarity.UNCOMMON )
			.isImmuneToFire() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack stack, @Nullable World world, List< ITextComponent > toolTip, ITooltipFlag flag ) {
		MajruszsHelper.addExtraTooltipIfDisabled( toolTip, Instances.END_SHARD_ORE.isEnabled() );
		MajruszsHelper.addAdvancedTooltip( toolTip, flag, TOOLTIP_TRANSLATION_KEY );
	}
}
