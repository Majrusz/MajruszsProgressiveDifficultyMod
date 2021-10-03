package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.features.end_items.EndItems;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/** New late game armor. */
public class EndArmorItem extends ArmorItem {
	private static final String ARMOR_TICK_TAG = "EndArmorTickCounter";

	public EndArmorItem( EquipmentSlot slot ) {
		super( CustomArmorMaterial.END, slot, ( new Item.Properties() ).tab( Instances.ITEM_GROUP )
			.rarity( Rarity.UNCOMMON )
			.fireResistant() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		MajruszsHelper.addExtraTextIfItemIsDisabled( tooltip, Instances.END_SHARD_ORE.isEnabled() );

		if( Minecraft.getInstance().player != null )
			EndItems.addEndArmorTooltip( Minecraft.getInstance().player, tooltip );
	}

	/** Returns path to End Armor texture. */
	@Nullable
	@Override
	public String getArmorTexture( ItemStack stack, Entity entity, EquipmentSlot slot, String type ) {
		CompoundTag data = entity.getPersistentData();
		data.putInt( ARMOR_TICK_TAG, ( data.getInt( ARMOR_TICK_TAG ) + 1 ) % ( 80 * 4 ) );
		String register = "textures/models/armor/end_layer_";
		register += ( slot == EquipmentSlot.LEGS ? "2" : "1" ) + "_";
		register += ( "" + ( 1 + data.getInt( ARMOR_TICK_TAG ) / 80 ) ) + ".png";

		ResourceLocation textureLocation = MajruszsDifficulty.getLocation( register );
		return textureLocation.toString();
	}
}
