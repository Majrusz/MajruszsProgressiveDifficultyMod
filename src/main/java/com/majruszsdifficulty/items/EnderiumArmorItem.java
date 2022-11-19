package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import javax.annotation.Nullable;

public class EnderiumArmorItem extends ArmorItem {
	private static final String ARMOR_TICK_TAG = "EndArmorTickCounter";

	public EnderiumArmorItem( EquipmentSlot slot ) {
		super( CustomArmorMaterial.END, slot, new Item.Properties().tab( Registries.ITEM_GROUP ).rarity( Rarity.UNCOMMON ).fireResistant() );
	}

	@Nullable
	@Override
	public String getArmorTexture( ItemStack stack, Entity entity, EquipmentSlot slot, String type ) {
		CompoundTag data = entity.getPersistentData();
		data.putInt( ARMOR_TICK_TAG, ( data.getInt( ARMOR_TICK_TAG ) + 1 ) % ( 80 * 4 ) );
		String register = "textures/models/armor/enderium_layer_";
		register += ( slot == EquipmentSlot.LEGS ? "2" : "1" ) + "_";
		register += ( "" + ( 1 + data.getInt( ARMOR_TICK_TAG ) / 80 ) ) + ".png";

		return Registries.getLocationString( register );
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

		@Override
		public boolean isEnderMask( ItemStack stack, Player player, EnderMan endermanEntity ) {
			return false;//Registries.ENDERIUM_SET.countSetItems( player ) >= 2;
		}
	}

	public static class Leggings extends EnderiumArmorItem {
		public Leggings() {
			super( EquipmentSlot.LEGS );
		}
	}
}
