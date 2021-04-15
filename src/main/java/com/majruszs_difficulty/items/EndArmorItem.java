package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/** New late game armor. */
public class EndArmorItem extends ArmorItem {
	public EndArmorItem( EquipmentSlotType slot ) {
		super( CustomArmorMaterial.END, slot, ( new Item.Properties() ).tab( Instances.ITEM_GROUP ) );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack stack, @Nullable World world, List< ITextComponent > toolTip, ITooltipFlag flag ) {
		MajruszsDifficulty.addExtraTooltipIfDisabled( toolTip, Instances.END_SHARD_ORE.isEnabled() );
	}
}
