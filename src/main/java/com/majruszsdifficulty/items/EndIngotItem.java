package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Instances;
import com.majruszsdifficulty.MajruszsHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/** New strong end-game ingot. */
public class EndIngotItem extends Item {
	public EndIngotItem() {
		super( new Properties().tab( Instances.ITEM_GROUP ).rarity( Rarity.UNCOMMON ) );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		MajruszsHelper.addExtraTextIfItemIsDisabled( tooltip, Instances.END_SHARD_ORE.isEnabled() );
	}
}
