package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.stream.Stream;

public class CerberusFangItem extends Item {
	public CerberusFangItem() {
		super( new Properties().tab( Registries.ITEM_GROUP ) );
	}

	@Override
	public void fillItemCategory( CreativeModeTab creativeModeTab, NonNullList< ItemStack > itemStacks ) {
		super.fillItemCategory( creativeModeTab, itemStacks );

		if( !this.allowedIn( creativeModeTab ) ) {
			return;
		}

		Stream.of(
			newItemStackWithPotion( Items.TIPPED_ARROW, Registries.WITHER_POTION.get() ),
			newItemStackWithPotion( Items.TIPPED_ARROW, Registries.WITHER_POTION_LONG.get() ),
			newItemStackWithPotion( Items.TIPPED_ARROW, Registries.WITHER_POTION_STRONG.get() ),
			newItemStackWithPotion( Items.POTION, Registries.WITHER_POTION.get() ),
			newItemStackWithPotion( Items.POTION, Registries.WITHER_POTION_LONG.get() ),
			newItemStackWithPotion( Items.POTION, Registries.WITHER_POTION_STRONG.get() ),
			newItemStackWithPotion( Items.SPLASH_POTION, Registries.WITHER_POTION.get() ),
			newItemStackWithPotion( Items.SPLASH_POTION, Registries.WITHER_POTION_LONG.get() ),
			newItemStackWithPotion( Items.SPLASH_POTION, Registries.WITHER_POTION_STRONG.get() ),
			newItemStackWithPotion( Items.LINGERING_POTION, Registries.WITHER_POTION.get() ),
			newItemStackWithPotion( Items.LINGERING_POTION, Registries.WITHER_POTION_LONG.get() ),
			newItemStackWithPotion( Items.LINGERING_POTION, Registries.WITHER_POTION_STRONG.get() )
		).forEach( itemStacks::add );
	}

	private static ItemStack newItemStackWithPotion( Item item, Potion potion ) {
		return PotionUtils.setPotion( new ItemStack( item ), potion );
	}
}
