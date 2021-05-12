package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/** New late game sword. */
public class EndSwordItem extends SwordItem {
	public EndSwordItem() {
		super( CustomItemTier.END, 3, -2.4f, ( new Item.Properties() ).group( Instances.ITEM_GROUP )
			.isImmuneToFire() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack stack, @Nullable World world, List< ITextComponent > toolTip, ITooltipFlag flag ) {
		MajruszsDifficulty.addExtraTooltipIfDisabled( toolTip, Instances.END_SHARD_ORE.isEnabled() );

		if( !flag.isAdvanced() )
			return;

		toolTip.add( new TranslationTextComponent( "item.majruszs_difficulty.end_sword.item_tooltip" ).mergeStyle( TextFormatting.GRAY ) );
	}
}
