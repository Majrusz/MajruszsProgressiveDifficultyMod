package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.mlib.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

import static com.majruszsdifficulty.gamemodifiers.list.WitherSwordEffect.WITHER;

public class WitherSwordItem extends SwordItem {
	private final static String TOOLTIP_TRANSLATION_KEY = "item.majruszsdifficulty.wither_sword.tooltip";

	public WitherSwordItem() {
		super( CustomItemTier.WITHER, 3, -2.4f, new Properties().tab( Registries.ITEM_GROUP ).rarity( Rarity.UNCOMMON ) );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		if( !flag.isAdvanced() )
			return;

		tooltip.add( Component.translatable( TOOLTIP_TRANSLATION_KEY, Utility.toRoman( WITHER.getAmplifier() ), Utility.ticksToSeconds( WITHER.getDuration() ) )
			.withStyle( ChatFormatting.GRAY ) );
	}
}
