package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.features.end_items.EndItems;
import com.majruszs_difficulty.features.end_items.HasteOnDestroyingBlocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/** New late game pickaxe. */
public class EndPickaxeItem extends PickaxeItem {
	public EndPickaxeItem() {
		super( CustomItemTier.END, 1, -2.8f, ( new Properties() ).group( Instances.ITEM_GROUP ).rarity( Rarity.UNCOMMON )
			.isImmuneToFire() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack itemStack, @Nullable World world, List< ITextComponent > tooltip, ITooltipFlag flag ) {
		MajruszsHelper.addExtraTooltipIfDisabled( tooltip, Instances.END_SHARD_ORE.isEnabled() );
		MajruszsHelper.addAdvancedTooltips( tooltip, flag, EndItems.Keys.HASTE_TOOLTIP, EndItems.Keys.LEVITATION_TOOLTIP );
	}
}
