package com.majruszs_difficulty.events.undead_army;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.*;

import java.util.List;

/** Easier handling formatted text for Undead Army. */
public class UndeadArmyText {
	public static final ITextComponent TITLE = new TranslationTextComponent( "majruszs_difficulty.undead_army.title" );
	public static final ITextComponent WAVE = new TranslationTextComponent( "majruszs_difficulty.undead_army.wave" );
	public static final ITextComponent BETWEEN_WAVES = new TranslationTextComponent( "majruszs_difficulty.undead_army.between_waves" );
	public static final ITextComponent VICTORY = new TranslationTextComponent( "majruszs_difficulty.undead_army.victory" );
	public static final ITextComponent FAILED = new TranslationTextComponent( "majruszs_difficulty.undead_army.failed" );
	public static final ITextComponent APPROACHING = new TranslationTextComponent( "majruszs_difficulty.undead_army.approaching" );

	/** Returns formatted text with information about current wave. */
	public static ITextComponent getWaveMessage( int currentWave ) {
		IFormattableTextComponent message = new StringTextComponent( "" );
		message.append( TITLE );
		message.appendString( " (" );
		message.append( WAVE );
		message.appendString( " " + currentWave + ")" );

		return message;
	}

	/** Sends information about Undead Army start to every given player. */
	public static void notifyAboutStart( List< ServerPlayerEntity > players, Direction direction ) {
		String directionAsString = direction.toString();

		IFormattableTextComponent message = APPROACHING.copyRaw();
		message.appendString( " " );
		message.append( new TranslationTextComponent( "majruszs_difficulty.undead_army." + directionAsString.toLowerCase() ) );
		message.appendString( "!" );
		message.mergeStyle( TextFormatting.BOLD );
		message.mergeStyle( TextFormatting.DARK_PURPLE );

		for( ServerPlayerEntity player : players )
			player.sendStatusMessage( message, false );
	}
}
