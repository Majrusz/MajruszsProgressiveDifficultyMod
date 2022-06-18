package com.majruszsdifficulty.items;

import com.majruszsdifficulty.MajruszsHelper;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.features.end_items.EndItems;
import com.mlib.client.ClientHelper;
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

/** New late game pickaxe. */
public class EndPickaxeItem extends PickaxeItem {
	public EndPickaxeItem() {
		super( CustomItemTier.END, 1, -2.8f, ( new Properties() ).tab( Registries.ITEM_GROUP ).rarity( Rarity.UNCOMMON ).fireResistant() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		if( ClientHelper.isShiftDown() ) {
			MajruszsHelper.addTranslatableTexts( tooltip, EndItems.Keys.HASTE_TOOLTIP, EndItems.Keys.LEVITATION_TOOLTIP );
		} else {
			MajruszsHelper.addMoreDetailsText( tooltip );
		}
	}
}
