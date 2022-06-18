package com.majruszsdifficulty.features.item_sets;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BaseSet {
	public static final List< BaseSet > SET_LIST = new ArrayList<>();
	protected ItemData[] itemData;
	protected BonusData[] bonusData;
	protected ChatFormatting chatFormatting;
	protected String setTranslationKey;

	public static boolean isFromAnySet( ItemStack itemStack ) {
		for( BaseSet set : SET_LIST )
			for( ItemData itemData : set.itemData )
				if( itemData.isValidItem( itemStack ) )
					return true;

		return false;
	}

	public int countSetItems( Player player ) {
		int sum = 0;
		for( ItemData itemData : this.itemData )
			if( itemData.hasItemEquipped( player ) )
				++sum;

		return sum;
	}

	public boolean isSetItem( ItemStack itemStack ) {
		for( ItemData itemData : this.itemData )
			if( itemData.isValidItem( itemStack ) )
				return true;

		return false;
	}
}
