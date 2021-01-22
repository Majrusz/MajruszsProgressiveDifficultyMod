package com.majruszs_difficulty.events.undead_army;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.*;

import java.util.List;

/** Easier handling formatted text for Undead Army. */
public class TextManager {
	public final ITextComponent title = new TranslationTextComponent( "majruszs_difficulty.undead_army.title" );
	public final ITextComponent wave = new TranslationTextComponent( "majruszs_difficulty.undead_army.wave" );
	public final ITextComponent between_waves = new TranslationTextComponent( "majruszs_difficulty.undead_army.between_waves" );
	public final ITextComponent victory = new TranslationTextComponent( "majruszs_difficulty.undead_army.victory" );
	public final ITextComponent failed = new TranslationTextComponent( "majruszs_difficulty.undead_army.failed" );
	public final ITextComponent approaching = new TranslationTextComponent( "majruszs_difficulty.undead_army.approaching" );

	public ITextComponent getWaveMessage( int currentWave ) {
		IFormattableTextComponent message = new StringTextComponent( "" );
		message.append( this.title );
		message.appendString( " (" );
		message.append( this.wave );
		message.appendString( " " + currentWave + ")" );

		return message;
	}

	public void notifyAboutStart( List< ServerPlayerEntity > players, Direction direction ) {
		IFormattableTextComponent message = ( IFormattableTextComponent )this.approaching;
		message.appendString( " " );
		message.append( new TranslationTextComponent( "majruszs_difficulty.undead_army." + direction.toString()
			.toLowerCase() ) );
		message.appendString( "!" );
		message.mergeStyle( TextFormatting.BOLD );
		message.mergeStyle( TextFormatting.DARK_PURPLE );

		for( ServerPlayerEntity player : players )
			player.sendStatusMessage( message, false );
	}
}
