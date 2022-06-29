package com.majruszsdifficulty.accessories;

import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.attributes.AttributeHandler;
import com.mlib.config.IntegerConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnPlayerTickContext;
import com.mlib.gamemodifiers.data.OnPlayerTickData;
import com.mlib.text.TextHelper;
import com.mlib.time.TimeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FishingLuckBonus extends AccessoryBonus {
	static final String BONUS_KEY = "item.majruszsdifficulty.angler_emblem.item_tooltip";

	public FishingLuckBonus() {
		this.setTooltipConsumer( this::addTooltips );
	}

	private void addTooltips( ItemStack itemStack, List< Component > tooltip ) {
		List< FormattedText > decomposedText = TextHelper.decomposeTranslatableText( BONUS_KEY, "420" );
		MutableComponent luckText = TextHelper.createColoredTranslatable( decomposedText, ChatFormatting.GRAY, new TextHelper.FormattingData( 1, ChatFormatting.AQUA ) );
		tooltip.add( luckText );
	}

	public static class Modifier extends GameModifier {
		static final AttributeHandler LUCK_ATTRIBUTE = new AttributeHandler( "4010270c-9d57-4273-8a41-00985f1e4781", "AnglerEmblemLuckBonus", Attributes.LUCK, AttributeModifier.Operation.ADDITION );
		final IntegerConfig luckValue = new IntegerConfig( "luck", "Luck bonus applied when a player is fishing.", false, 3, 1, 10 );

		public Modifier() {
			super( GameModifier.ACCESSORY, "AnglerEmblem", "" );

			OnPlayerTickContext onTick = new OnPlayerTickContext( this::updateLuckBonus );
			onTick.addCondition( new Condition.ContextOnPlayerTick( data->data.level != null ) )
				.addCondition( new Condition.ContextOnPlayerTick( data->TimeHelper.hasServerSecondsPassed( 0.5 ) ) )
				.addConfig( this.luckValue );

			this.addContexts( onTick );
		}

		private void updateLuckBonus( OnPlayerTickData data ) {
			LUCK_ATTRIBUTE.setValueAndApply( data.player, this.luckValue.get() );
		}
	}
}
