package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import javax.annotation.Nullable;

/** New late game armor. */
public class EndArmorItem extends ArmorItem {
	private static final String ARMOR_TICK_TAG = "EndArmorTickCounter";

	public EndArmorItem( EquipmentSlot slot ) {
		super( CustomArmorMaterial.END, slot, ( new Item.Properties() ).tab( Registries.ITEM_GROUP ).rarity( Rarity.UNCOMMON ).fireResistant() );
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

		ResourceLocation textureLocation = Registries.getLocation( register );
		return textureLocation.toString();
	}
}
