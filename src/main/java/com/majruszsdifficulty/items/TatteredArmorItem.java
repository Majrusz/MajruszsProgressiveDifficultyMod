package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class TatteredArmorItem extends ArmorItem {
	public TatteredArmorItem( EquipmentSlot slot ) {
		super( CustomArmorMaterial.UNDEAD, slot, new Item.Properties().rarity( Rarity.UNCOMMON ).tab( Registries.ITEM_GROUP ) );
	}

	@Nullable
	@Override
	public String getArmorTexture( ItemStack stack, Entity entity, EquipmentSlot slot, String type ) {
		return Registries.getLocationString( String.format( "textures/models/armor/tattered_layer_%d.png", slot == EquipmentSlot.LEGS ? 2 : 1 ) );
	}

	public static class Boots extends TatteredArmorItem {
		public Boots() {
			super( EquipmentSlot.FEET );
		}

		@Override
		public boolean canWalkOnPowderedSnow( ItemStack itemStack, LivingEntity wearer ) {
			return itemStack.getItem().equals( this );
		}
	}

	public static class Chestplate extends TatteredArmorItem {
		public Chestplate() {
			super( EquipmentSlot.CHEST );
		}
	}

	public static class Helmet extends TatteredArmorItem {
		public Helmet() {
			super( EquipmentSlot.HEAD );
		}
	}

	public static class Leggings extends TatteredArmorItem {
		public Leggings() {
			super( EquipmentSlot.LEGS );
		}
	}
}
