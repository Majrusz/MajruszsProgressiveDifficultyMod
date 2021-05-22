package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/** New strong end-game ingot. */
public class EndIngotItem extends Item {
	public EndIngotItem() {
		super( new Properties().group( Instances.ITEM_GROUP )
			.rarity( Rarity.UNCOMMON ) );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack itemStack, @Nullable World world, List< ITextComponent > tooltip, ITooltipFlag flag ) {
		MajruszsHelper.addExtraTooltipIfDisabled( tooltip, Instances.END_SHARD_ORE.isEnabled() );
	}
}
