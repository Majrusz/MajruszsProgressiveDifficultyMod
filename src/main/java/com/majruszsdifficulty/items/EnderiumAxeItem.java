package com.majruszsdifficulty.items;

import com.majruszsdifficulty.MajruszsHelper;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.features.EnderiumItems;
import com.mlib.client.ClientHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class EnderiumAxeItem extends AxeItem {
	static final EnderiumItems.Tooltip ENDERIUM_TOOLTIP = new EnderiumItems.Tooltip( EnderiumItems.Keys.HASTE_TOOLTIP );

	public EnderiumAxeItem() {
		super( CustomItemTier.END, 6.0f, -3.1f, new Properties().rarity( Rarity.UNCOMMON ).tab( Registries.ITEM_GROUP ).fireResistant() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		ENDERIUM_TOOLTIP.apply( tooltip );
	}
}
