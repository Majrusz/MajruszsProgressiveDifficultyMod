package com.majruszsdifficulty.features.item_sets;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseSet {
	public static final List< BaseSet > SET_LIST = new ArrayList<>();
	protected final ItemData[] itemData;
	protected final BonusData[] bonusData;
	protected final ChatFormatting chatFormatting;
	protected final String translationKey;

	public BaseSet( ItemData[] itemData, BonusData[] bonusData, ChatFormatting chatFormatting, String translationKey ) {
		this.itemData = itemData;
		this.bonusData = bonusData;
		this.chatFormatting = chatFormatting;
		this.translationKey = translationKey;

		SET_LIST.add( this );
	}

	public static boolean isFromAnySet( ItemStack itemStack ) {
		return SET_LIST.stream().anyMatch( set -> set.isSetItem( itemStack ) );
	}

	public int countSetItems( Player player ) {
		int sum = 0;
		for( ItemData itemData : this.itemData )
			if( itemData.hasItemEquipped( player ) )
				++sum;

		return sum;
	}

	public boolean isSetItem( ItemStack itemStack ) {
		return Arrays.stream( this.itemData ).anyMatch( itemData -> itemData.isSetItemStack( itemStack ) );
	}

	public boolean areRequirementsMet( Player player, BonusData bonusData ) {
		return bonusData.condition.validate( this, player );
	}

	public MutableComponent getTranslatedName() {
		return Component.translatable( this.translationKey );
	}

	public ChatFormatting getChatFormatting() {
		return this.chatFormatting;
	}
}
