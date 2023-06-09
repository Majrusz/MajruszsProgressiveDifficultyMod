package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import javax.annotation.Nullable;

public class TatteredArmorItem extends ArmorItem {
	public TatteredArmorItem( ArmorItem.Type type ) {
		super( CustomArmorMaterial.UNDEAD, type, new Item.Properties().rarity( Rarity.UNCOMMON ) );
	}

	@Nullable
	@Override
	public String getArmorTexture( ItemStack stack, Entity entity, EquipmentSlot slot, String type ) {
		return Registries.getLocationString( String.format( "textures/models/armor/tattered_layer_%d.png", slot == EquipmentSlot.LEGS ? 2 : 1 ) );
	}

	public static class Boots extends TatteredArmorItem {
		public Boots() {
			super( Type.BOOTS );
		}
	}

	public static class Chestplate extends TatteredArmorItem {
		public Chestplate() {
			super( Type.CHESTPLATE );
		}
	}

	public static class Helmet extends TatteredArmorItem {
		public Helmet() {
			super( Type.HELMET );
		}
	}

	public static class Leggings extends TatteredArmorItem {
		public Leggings() {
			super( Type.LEGGINGS );
		}
	}
}
