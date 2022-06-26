package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.EnderiumItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class EnderiumPickaxeItem extends PickaxeItem {
	static final EnderiumItems.Tooltip ENDERIUM_TOOLTIP = new EnderiumItems.Tooltip( EnderiumItems.Keys.HASTE_TOOLTIP );

	public EnderiumPickaxeItem() {
		super( CustomItemTier.END, 1, -2.8f, new Properties().tab( Registries.ITEM_GROUP ).rarity( Rarity.UNCOMMON ).fireResistant() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		ENDERIUM_TOOLTIP.apply( tooltip );
	}
}
