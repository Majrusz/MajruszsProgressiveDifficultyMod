package com.majruszsdifficulty.features.item_sets;

import com.majruszsdifficulty.MajruszsHelper;
import com.mlib.client.ClientHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@OnlyIn( Dist.CLIENT )
@Mod.EventBusSubscriber( value = Dist.CLIENT )
public class SetsTooltipUpdater {
	private static final ChatFormatting DISABLED_FORMAT = ChatFormatting.DARK_GRAY;
	private static final ChatFormatting HINT_FORMAT = ChatFormatting.GRAY;
	private static final String SET_TOOLTIP = "majruszsdifficulty.items.set_list_tooltip";
	private static final String BONUS_TOOLTIP = "majruszsdifficulty.items.set_bonus_tooltip";

	@SubscribeEvent
	public static void onItemTooltip( ItemTooltipEvent event ) {
		Player player = event.getPlayer();
		ItemStack itemStack = event.getItemStack();
		List< Component > tooltip = event.getToolTip();

		boolean isFromAnySet = BaseSet.isFromAnySet( itemStack );
		if( !isFromAnySet )
			return;

		if( !ClientHelper.isShiftDown() ) {
			MajruszsHelper.addEmptyLine( tooltip );
			MajruszsHelper.addMoreDetailsText( tooltip );
			return;
		}

		for( BaseSet set : BaseSet.SET_LIST ) {
			if( !set.isSetItem( itemStack ) )
				continue;

			int equippedSetItems = set.countSetItems( player );
			String setText = Component.translatable( set.setTranslationKey ).getString();

			MajruszsHelper.addEmptyLine( tooltip );
			tooltip.add( Component.translatable( SET_TOOLTIP, setText, equippedSetItems, set.itemData.length ).withStyle( HINT_FORMAT ) );
			for( ItemData itemData : set.itemData ) {
				ChatFormatting chatFormatting = itemData.hasItemEquipped( player ) ? set.chatFormatting : DISABLED_FORMAT;

				tooltip.add( getFormattedItemName( itemData.item, chatFormatting ) );
			}

			MajruszsHelper.addEmptyLine( tooltip );
			tooltip.add( Component.translatable( BONUS_TOOLTIP ).withStyle( HINT_FORMAT ) );
			for( BonusData bonusData : set.bonusData ) {
				boolean isEnabled = equippedSetItems >= bonusData.requiredItems && bonusData.extraValidator.validate( player );
				ChatFormatting chatFormatting = isEnabled ? set.chatFormatting : DISABLED_FORMAT;

				tooltip.add( getFormattedBonus( bonusData.translationKey, bonusData.requiredItems, set.itemData.length, chatFormatting ) );
			}
		}
	}

	protected static MutableComponent getFormattedItemName( Item item, ChatFormatting chatFormatting ) {
		return Component.literal( " " ).append( item.getDescription().copy().withStyle( chatFormatting ) );
	}

	protected static MutableComponent getFormattedBonus( String translationKey, int amountRequired, int totalAmount, ChatFormatting chatFormatting ) {
		return Component.translatable( translationKey, amountRequired, totalAmount ).withStyle( chatFormatting );
	}
}
