package com.majruszsdifficulty.items;

import com.majruszsdifficulty.MajruszsHelper;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.features.end_items.EndItems;
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

/** New late game axe. */
public class EndAxeItem extends AxeItem {
	public EndAxeItem() {
		super( CustomItemTier.END, 7.0f, -3.1f, ( new Properties() ).rarity( Rarity.UNCOMMON ).tab( Registries.ITEM_GROUP ).fireResistant() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		if( ClientHelper.isShiftDown() ) {
			MajruszsHelper.addTranslatableTexts( tooltip, EndItems.Keys.BLEED_TOOLTIP, EndItems.Keys.HASTE_TOOLTIP );
		} else {
			MajruszsHelper.addMoreDetailsText( tooltip );
		}
	}
}
