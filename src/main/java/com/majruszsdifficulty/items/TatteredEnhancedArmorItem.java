package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import javax.annotation.Nullable;

public class TatteredEnhancedArmorItem extends ArmorItem {
	public TatteredEnhancedArmorItem( EquipmentSlot slot ) {
		super( CustomArmorMaterial.UNDEAD, slot, new Properties().rarity( Rarity.RARE ) );
	}

	@Nullable
	@Override
	public String getArmorTexture( ItemStack stack, Entity entity, EquipmentSlot slot, String type ) {
		return Registries.getLocationString( String.format( "textures/models/armor/tattered_enhanced_layer_%d.png", slot == EquipmentSlot.LEGS ? 2 : 1 ) );
	}

	public static class Chestplate extends TatteredEnhancedArmorItem {
		public Chestplate() {
			super( EquipmentSlot.CHEST );
		}
	}

	public static class Leggings extends TatteredEnhancedArmorItem {
		public Leggings() {
			super( EquipmentSlot.LEGS );
		}
	}
}
