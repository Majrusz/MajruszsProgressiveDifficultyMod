package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import javax.annotation.Nullable;

public class EnderiumArmorItem extends ArmorItem {
	public EnderiumArmorItem( ArmorItem.Type type ) {
		super( CustomArmorMaterial.END, type, new Item.Properties()
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
			super( Type.BOOTS );
		}
	}

	public static class Chestplate extends EnderiumArmorItem {
		public Chestplate() {
			super( Type.CHESTPLATE );
		}
	}

	public static class Helmet extends EnderiumArmorItem {
		public Helmet() {
			super( Type.HELMET );
		}
	}

	public static class Leggings extends EnderiumArmorItem {
		public Leggings() {
			super( Type.LEGGINGS );
		}
	}

	@AutoInstance
	public static class AddToEndCity {
		static final ResourceLocation ID = Registries.getLocation( "gameplay/enderium_upgrade_smithing_template" );

		public AddToEndCity() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
				.name( "EnderiumUpgradeSmithingTemplate" )
				.comment( "Determines where Enderium Upgrade can be found." );

			OnLoot.listen( this::addToChest )
				.addCondition( Condition.isServer() )
				.addCondition( OnLoot.is( BuiltInLootTables.END_CITY_TREASURE ) )
				.insertTo( group );
		}

		private void addToChest( OnLoot.Data data ) {
			data.addAsChestLoot( ID );
		}
	}
}
