package com.majruszsdifficulty.features.item_sets;

import com.mlib.ObfuscationGetter;
import com.mlib.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnegative;
import java.util.ArrayList;
import java.util.List;

public class BonusData {
	static final ObfuscationGetter.Field< TranslatableContents, List< FormattedText > > DECOMPOSED_PARTS = new ObfuscationGetter.Field<>( TranslatableContents.class, "f_237500_" );
	static final ObfuscationGetter.Method< TranslatableContents > DECOMPOSE = new ObfuscationGetter.Method<>( TranslatableContents.class, "m_237524_" );
	public final int requiredItems;
	public final String translationKey;
	public final ICondition condition;
	public final Parameter[] parameters;

	public BonusData( int requiredItems, String translationKey, ICondition condition, Parameter ...parameters ) {
		this.requiredItems = requiredItems;
		this.translationKey = translationKey;
		this.condition = condition.and( ( set, player ) -> set.countSetItems( player ) >= requiredItems );
		this.parameters = parameters;
	}

	public BonusData( int requiredItems, String translationKey, Parameter ...parameters ) {
		this( requiredItems, translationKey, ( set, player ) -> set.countSetItems( player ) >= requiredItems, parameters );
	}

	public BonusData( int requiredItems, String translationKey ) {
		this( requiredItems, translationKey, ( set, player ) -> set.countSetItems( player ) >= requiredItems );
	}

	public MutableComponent createTranslatedText( ChatFormatting defaultFormatting, ChatFormatting valueFormatting ) {
		MutableComponent component = Component.literal( "" ).withStyle( valueFormatting );
		List< String > params = new ArrayList<>();
		for( Parameter parameter : this.parameters )
			params.add( parameter.getFormat() );
		TranslatableContents translatableContents = new TranslatableContents( this.translationKey, params.toArray() );
		DECOMPOSE.invoke( translatableContents );
		List< FormattedText > formattedTexts = DECOMPOSED_PARTS.get( translatableContents );
		assert formattedTexts != null;
		for( int i = 0; i < formattedTexts.size(); ++i ) {
			ChatFormatting format = i % 2 == 1 && i / 2 < this.parameters.length ? valueFormatting : defaultFormatting;
			component.append( Component.literal( formattedTexts.get( i ).getString() ).withStyle( format ) );
		}

		return component;
	}

	public int asInt( @Nonnegative int parameterIdx ) {
		return this.parameters[ parameterIdx ].asInt();
	}

	public float asFloat( @Nonnegative int parameterIdx ) {
		return this.parameters[ parameterIdx ].asFloat();
	}

	public int asTicks( @Nonnegative int parameterIdx ) {
		return Utility.secondsToTicks( this.parameters[ parameterIdx ].asFloat() );
	}

	public interface ICondition {
		boolean validate( BaseSet set, Player player );

		default ICondition and( ICondition otherCondition ) {
			return ( set, player )->this.validate( set, player ) && otherCondition.validate( set, player );
		}

		default ICondition or( ICondition otherCondition ) {
			return ( set, player )->this.validate( set, player ) || otherCondition.validate( set, player );
		}
	}
}