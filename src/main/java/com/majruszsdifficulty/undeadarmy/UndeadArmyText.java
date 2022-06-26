package com.majruszsdifficulty.undeadarmy;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/** Easier handling formatted text for Undead Army. */
public class UndeadArmyText {
	public static final Component TITLE = Component.translatable( "majruszsdifficulty.undead_army.title" );
	public static final Component WAVE = Component.translatable( "majruszsdifficulty.undead_army.wave" );
	public static final Component BETWEEN_WAVES = Component.translatable( "majruszsdifficulty.undead_army.between_waves" );
	public static final Component VICTORY = Component.translatable( "majruszsdifficulty.undead_army.victory" );
	public static final Component FAILED = Component.translatable( "majruszsdifficulty.undead_army.failed" );
	public static final Component APPROACHING = Component.translatable( "majruszsdifficulty.undead_army.approaching" );

	/** Returns formatted text with information about current wave. */
	public static Component getWaveMessage( int currentWave ) {
		MutableComponent message = Component.literal( "" );
		message.append( TITLE );
		message.append( " (" );
		message.append( WAVE );
		message.append( " " + currentWave + ")" );

		return message;
	}

	/** Sends information about Undead Army start to every given player. */
	public static void notifyAboutStart( List< ServerPlayer > players, Direction direction ) {
		String directionAsString = direction.toString();

		MutableComponent message = APPROACHING.copy();
		message.append( " " );
		message.append( Component.translatable( "majruszsdifficulty.undead_army." + directionAsString.toLowerCase() ) );
		message.append( "!" );
		message.withStyle( ChatFormatting.BOLD );
		message.withStyle( ChatFormatting.DARK_PURPLE );

		for( ServerPlayer player : players )
			player.displayClientMessage( message, false );
	}
}
