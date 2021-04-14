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
		dispatcher.register( Commands.literal( "gamestate" )
			.requires( source->source.hasPermission( 4 ) )
			.then( Commands.literal( "normal" )
				.executes( entity->changeState( entity.getSource(), GameState.State.NORMAL ) ) )
			.then( Commands.literal( "expert" )
				.executes( entity->changeState( entity.getSource(), GameState.State.EXPERT ) ) )
			.then( Commands.literal( "master" )
				.executes( entity->changeState( entity.getSource(), GameState.State.MASTER ) ) ) );

		dispatcher.register( Commands.literal( "gamestate" )
			.requires( source->source.hasPermission( 0 ) )
			.executes( entity->getCurrentState( entity.getSource() ) ) );
	}

	public static int getCurrentState( CommandSource source ) {
		source.sendSuccess( getFeedbackMessage( GameState.getCurrentMode(), "current" ), true );

		return GameState.convertStateToInteger( GameState.getCurrentMode() );
	}

	public static int changeState( CommandSource source, GameState.State state ) {
		if( GameState.changeMode( state ) )
			source.sendSuccess( getFeedbackMessage( state, "change" ), true );

		return GameState.convertStateToInteger( state );
	}

	private static IFormattableTextComponent getFeedbackMessage( GameState.State state, String translationPart ) {
		IFormattableTextComponent feedback = new TranslationTextComponent( "commands.game_state." + translationPart );

		IFormattableTextComponent feedback_mode;
		switch( state ) {
			default:
				feedback_mode = GameState.getNormalModeText();
				break;
			case EXPERT:
				feedback_mode = GameState.getExpertModeText();
				break;
			case MASTER:
				feedback_mode = GameState.getMasterModeText();
				break;
		}

		feedback.append( " " );
		feedback.append( feedback_mode );
		feedback.append( "!" );

		return feedback;
	}
}
