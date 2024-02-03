package com.majruszsdifficulty.items;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Rarity;

import java.util.function.Supplier;

public class EnderiumArmor extends ArmorItem {
	public static Supplier< EnderiumArmor > boots() {
		return ()->new EnderiumArmor( EquipmentSlot.FEET );
	}

	public static Supplier< EnderiumArmor > chestplate() {
		return ()->new EnderiumArmor( EquipmentSlot.CHEST );
	}

	public static Supplier< EnderiumArmor > helmet() {
		return ()->new EnderiumArmor( EquipmentSlot.HEAD );
	}

	public static Supplier< EnderiumArmor > leggings() {
		return ()->new EnderiumArmor( EquipmentSlot.LEGS );
	}

	private EnderiumArmor( EquipmentSlot slot ) {
		super( CustomArmorMaterial.ENDERIUM, slot, new Properties().rarity( Rarity.UNCOMMON ).fireResistant() );
	}
}
