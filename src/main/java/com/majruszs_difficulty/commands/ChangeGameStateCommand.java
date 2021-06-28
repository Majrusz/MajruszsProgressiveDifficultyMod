package com.majruszs_difficulty.commands;

import com.majruszs_difficulty.GameState;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/** Command responsible for changing current game state. */
public final class ChangeGameStateCommand {
	private ChangeGameStateCommand() {}

	public static void register( CommandDispatcher< CommandSource > dispatcher ) {
		dispatcher.register( Commands.literal( "currentgamestate" )
			.requires( source->source.hasPermissionLevel( 0 ) )
			.executes( entity->getCurrentState( entity.getSource() ) ) );

		dispatcher.register( Commands.literal( "gamestate" )
			.requires( source->source.hasPermissionLevel( 4 ) )
			.then( Commands.literal( "normal" )
				.executes( entity->changeState( entity.getSource(), GameState.State.NORMAL ) ) )
			.then( Commands.literal( "expert" )
				.executes( entity->changeState( entity.getSource(), GameState.State.EXPERT ) ) )
			.then( Commands.literal( "master" )
				.executes( entity->changeState( entity.getSource(), GameState.State.MASTER ) ) ) );
	}

	public static int getCurrentState( CommandSource source ) {
		source.sendFeedback( getFeedbackMessage( GameState.getCurrentMode(), "current" ), true );

		return GameState.convertStateToInteger( GameState.getCurrentMode() );
	}

	public static int changeState( CommandSource source, GameState.State state ) {
		if( GameState.changeModeWithAdvancement( state, source.getServer() ) )
			source.sendFeedback( getFeedbackMessage( state, "change" ), true );

		return GameState.convertStateToInteger( state );
	}

	private static IFormattableTextComponent getFeedbackMessage( GameState.State state, String translationPart ) {
		IFormattableTextComponent feedback = new TranslationTextComponent( "commands.game_state." + translationPart );
		IFormattableTextComponent feedback_mode = GameState.getGameStateText( state );

		feedback.appendString( " " );
		feedback.append( feedback_mode );
		feedback.appendString( "!" );

		return feedback;
	}
}
