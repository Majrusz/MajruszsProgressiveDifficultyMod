package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Instances;
import com.majruszsdifficulty.MajruszsHelper;
import com.majruszsdifficulty.features.end_items.EndItems;
import com.mlib.client.ClientHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/** New late game sword. */
public class EndSwordItem extends SwordItem {
	public EndSwordItem() {
		super( CustomItemTier.END, 3, -2.4f, ( new Item.Properties() ).tab( Instances.ITEM_GROUP ).rarity( Rarity.UNCOMMON ).fireResistant() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack stack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		MajruszsHelper.addExtraTextIfItemIsDisabled( tooltip, Instances.END_SHARD_ORE.isEnabled() );
		if( ClientHelper.isShiftDown() ) {
			MajruszsHelper.addTranslatableTexts( tooltip, EndItems.Keys.BLEED_TOOLTIP, EndItems.Keys.HASTE_TOOLTIP );
		} else {
			MajruszsHelper.addMoreDetailsText( tooltip );
		}
	}
}
