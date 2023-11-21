package com.majruszsdifficulty.items;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Rarity;

import java.util.function.Supplier;

public class EnderiumArmor extends ArmorItem {
	public static Supplier< EnderiumArmor > boots() {
		return ()->new EnderiumArmor( Type.BOOTS );
	}

	public static Supplier< EnderiumArmor > chestplate() {
		return ()->new EnderiumArmor( Type.CHESTPLATE );
	}

	public static Supplier< EnderiumArmor > helmet() {
		return ()->new EnderiumArmor( Type.HELMET );
	}

	public static Supplier< EnderiumArmor > leggings() {
		return ()->new EnderiumArmor( Type.LEGGINGS );
	}

	private EnderiumArmor( ArmorItem.Type type ) {
		super( CustomArmorMaterial.ENDERIUM, type, new Properties().rarity( Rarity.UNCOMMON ).fireResistant() );
	}
}
