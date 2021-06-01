package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.events.HasteOnDestroyingBlocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/** New late game shovel. */
public class EndShovelItem extends ShovelItem {
	public EndShovelItem() {
		super( CustomItemTier.END, 1.5f, -3.0f, ( new Properties() ).group( Instances.ITEM_GROUP ).rarity( Rarity.UNCOMMON )
			.isImmuneToFire() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack itemStack, @Nullable World world, List< ITextComponent > tooltip, ITooltipFlag flag ) {
		MajruszsHelper.addExtraTooltipIfDisabled( tooltip, Instances.END_SHARD_ORE.isEnabled() );
		MajruszsHelper.addAdvancedTooltip( tooltip, flag, HasteOnDestroyingBlocks.getTooltipTranslationKey() );
	}
}
