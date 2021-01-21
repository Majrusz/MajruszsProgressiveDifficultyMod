package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class FishermanEmblemItem extends Item {
	public FishermanEmblemItem() {
		super( ( new Item.Properties() ).maxStackSize( 1 ).rarity( Rarity.RARE )
			.group( Instances.ITEM_GROUP ) );
	}

	/** Adding tooltip with information for what this embled does. */
	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack stack, @Nullable World world, List< ITextComponent > toolTip, ITooltipFlag flag ) {
		toolTip.add( new TranslationTextComponent( "item.majruszs_difficulty.fisherman_emblem.item_tooltip1" ).mergeStyle( TextFormatting.GOLD ) );
		toolTip.add( new TranslationTextComponent( "item.majruszs_difficulty.fisherman_emblem.item_tooltip2" ).mergeStyle( TextFormatting.GRAY ) );
	}
}
