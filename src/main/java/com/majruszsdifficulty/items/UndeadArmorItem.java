package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class UndeadArmorItem extends ArmorItem {
	public UndeadArmorItem( EquipmentSlot slot ) {
		super( CustomArmorMaterial.UNDEAD, slot, new Item.Properties().rarity( Rarity.UNCOMMON ) );
	}

	@Nullable
	@Override
	public String getArmorTexture( ItemStack stack, Entity entity, EquipmentSlot slot, String type ) {
		return Registries.getLocationString( String.format( "textures/models/armor/undead_army_layer_%d.png", slot == EquipmentSlot.LEGS ? 2 : 1 ) );
	}

	public static class Boots extends UndeadArmorItem {
		public Boots() {
			super( EquipmentSlot.FEET );
		}
	}

	public static class Chestplate extends UndeadArmorItem {
		public Chestplate() {
			super( EquipmentSlot.CHEST );
		}
	}

	public static class Helmet extends UndeadArmorItem {
		public Helmet() {
			super( EquipmentSlot.HEAD );
		}
	}

	public static class Leggings extends UndeadArmorItem {
		public Leggings() {
			super( EquipmentSlot.LEGS );
		}
	}
}
