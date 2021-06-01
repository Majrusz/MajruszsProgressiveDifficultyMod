package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.events.HasteOnDestroyingBlocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/** New late game axe. */
public class EndAxeItem extends AxeItem {
	public EndAxeItem() {
		super( CustomItemTier.END, 6.0f, -3.1f, ( new Properties() ).rarity( Rarity.UNCOMMON ).group( Instances.ITEM_GROUP )
			.isImmuneToFire() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack itemStack, @Nullable World world, List< ITextComponent > tooltip, ITooltipFlag flag ) {
		MajruszsHelper.addExtraTooltipIfDisabled( tooltip, Instances.END_SHARD_ORE.isEnabled() );
		MajruszsHelper.addAdvancedTooltip( tooltip, flag, HasteOnDestroyingBlocks.getTooltipTranslationKey() );
	}
}
