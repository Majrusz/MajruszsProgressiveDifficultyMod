package com.majruszsdifficulty.items;

import com.majruszsdifficulty.EnderiumItems;
import com.majruszsdifficulty.Registries;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class EnderiumShovelItem extends ShovelItem {
	public EnderiumShovelItem() {
		super( CustomItemTier.END, 1.5f, -3.0f, new Properties().tab( Registries.ITEM_GROUP ).rarity( Rarity.UNCOMMON ).fireResistant() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		new EnderiumItems.Tooltip( EnderiumItems.Keys.HASTE_TOOLTIP ).apply( tooltip );
	}
}
