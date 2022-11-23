package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import javax.annotation.Nullable;

public class EnderiumArmorItem extends ArmorItem {
	public EnderiumArmorItem( EquipmentSlot slot ) {
		super( CustomArmorMaterial.END, slot, new Item.Properties().tab( Registries.ITEM_GROUP )
			.rarity( Rarity.UNCOMMON )
			.fireResistant() );
	}

	@Nullable
	@Override
	public String getArmorTexture( ItemStack stack, Entity entity, EquipmentSlot slot, String type ) {
		return Registries.getLocationString( String.format( "textures/models/armor/enderium_layer_%d.png", slot == EquipmentSlot.LEGS ? 2 : 1 ) );
	}

	public static class Boots extends EnderiumArmorItem {
		public Boots() {
			super( EquipmentSlot.FEET );
		}
	}

	public static class Chestplate extends EnderiumArmorItem {
		public Chestplate() {
			super( EquipmentSlot.CHEST );
		}
	}

	public static class Helmet extends EnderiumArmorItem {
		public Helmet() {
			super( EquipmentSlot.HEAD );
		}
	}

	public static class Leggings extends EnderiumArmorItem {
		public Leggings() {
			super( EquipmentSlot.LEGS );
		}
	}
}
