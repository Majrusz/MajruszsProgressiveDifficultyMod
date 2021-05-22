package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.events.HasteOnDestroyingBlocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/** New late game axe. */
public class EndAxeItem extends AxeItem {
	public EndAxeItem() {
		super( CustomItemTier.END, 6.0f, -3.1f, ( new Properties() ).group( Instances.ITEM_GROUP )
			.isImmuneToFire() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack stack, @Nullable World world, List< ITextComponent > toolTip, ITooltipFlag flag ) {
		MajruszsHelper.addExtraTooltipIfDisabled( toolTip, Instances.END_SHARD_ORE.isEnabled() );

		if( !flag.isAdvanced() )
			return;

		toolTip.add( HasteOnDestroyingBlocks.getTooltip() );
	}
}
