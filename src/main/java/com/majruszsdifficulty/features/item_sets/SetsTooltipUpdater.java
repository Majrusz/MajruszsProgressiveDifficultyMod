package com.majruszsdifficulty.features.item_sets;

import com.majruszsdifficulty.MajruszsHelper;
import com.mlib.client.ClientHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
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
	private static final String ITEM_PROGRESS_TOOLTIP = "majruszsdifficulty.items.set_item_progress";
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

			MajruszsHelper.addEmptyLine( tooltip );
			addItemList( tooltip, set, player );

			MajruszsHelper.addEmptyLine( tooltip );
			addBonusList( tooltip, set, player );
		}
	}

	protected static void addItemList( List< Component > tooltip, BaseSet set, Player player ) {
		tooltip.add( Component.translatable( SET_TOOLTIP, set.getTranslatedName(), set.countSetItems( player ), set.itemData.length )
			.withStyle( HINT_FORMAT ) );
		for( ItemData itemData : set.itemData ) {
			ChatFormatting chatFormatting = itemData.hasItemEquipped( player ) ? set.getChatFormatting() : DISABLED_FORMAT;

			tooltip.add( Component.literal( " " ).append( itemData.getTranslatedName() ).withStyle( chatFormatting ) );
		}
	}

	protected static void addBonusList( List< Component > tooltip, BaseSet set, Player player ) {
		tooltip.add( Component.translatable( BONUS_TOOLTIP ).withStyle( HINT_FORMAT ) );
		for( BonusData bonusData : set.bonusData ) {
			boolean metRequirements = set.areRequirementsMet( player, bonusData );
			ChatFormatting chatFormatting = metRequirements ? set.chatFormatting : DISABLED_FORMAT;

			MutableComponent component = Component.literal( " " )
				.append( Component.translatable( ITEM_PROGRESS_TOOLTIP, bonusData.requiredItems, set.itemData.length ).withStyle( chatFormatting ) )
				.append( Component.literal( " " ) )
				.append( bonusData.createTranslatedText( metRequirements ? HINT_FORMAT : DISABLED_FORMAT, metRequirements ? set.chatFormatting : DISABLED_FORMAT ) );
			tooltip.add( component );
		}
	}
}
