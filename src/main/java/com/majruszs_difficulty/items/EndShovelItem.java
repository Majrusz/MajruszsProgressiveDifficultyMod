package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.features.end_items.EndItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

/** New late game shovel. */
public class EndShovelItem extends ShovelItem {
	public EndShovelItem() {
		super( CustomItemTier.END, 1.5f, -3.0f, ( new Properties() ).tab( Instances.ITEM_GROUP )
			.rarity( Rarity.UNCOMMON )
			.fireResistant() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		MajruszsHelper.addExtraTooltipIfDisabled( tooltip, Instances.END_SHARD_ORE.isEnabled() );
		MajruszsHelper.addAdvancedTooltips( tooltip, flag, EndItems.Keys.HASTE_TOOLTIP, EndItems.Keys.LEVITATION_TOOLTIP );
	}
}
