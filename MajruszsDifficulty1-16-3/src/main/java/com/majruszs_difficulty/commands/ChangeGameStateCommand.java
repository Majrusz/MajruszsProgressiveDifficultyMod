package com.majruszs_difficulty.commands;

import com.majruszs_difficulty.GameState;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public final class ChangeGameStateCommand {
	private ChangeGameStateCommand() {}

	public static void register( CommandDispatcher< CommandSource > dispatcher ) {
		dispatcher.register( Commands.literal( "gamestate" )
			.requires( source->source.hasPermissionLevel( 4 ) )
			.then( Commands.literal( "normal" )
				.executes( entity->changeState( entity.getSource(), GameState.Mode.NORMAL ) ) )
			.then( Commands.literal( "expert" )
				.executes( entity->changeState( entity.getSource(), GameState.Mode.EXPERT ) ) )
			.then( Commands.literal( "master" )
				.executes( entity->changeState( entity.getSource(), GameState.Mode.MASTER ) ) ) );

		dispatcher.register( Commands.literal( "gamestate" )
			.requires( source->source.hasPermissionLevel( 0 ) )
			.executes( entity->getCurrentState( entity.getSource() ) ) );
	}

	public static int getCurrentState( CommandSource source ) {
		source.sendFeedback( getFeedbackMessage( GameState.getCurrentMode(), "current" ), true );

		return GameState.convertModeToInteger( GameState.getCurrentMode() );
	}

	public static int changeState( CommandSource source, GameState.Mode mode ) {
		if( GameState.changeMode( mode ) )
			source.sendFeedback( getFeedbackMessage( mode, "change" ), true );

		return GameState.convertModeToInteger( mode );
	}

	private static IFormattableTextComponent getFeedbackMessage( GameState.Mode mode, String translationPart ) {
		IFormattableTextComponent feedback = new TranslationTextComponent( "commands.game_state." + translationPart );

		IFormattableTextComponent feedback_mode;
		switch( mode ) {
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

		feedback.func_240702_b_( " " );
		feedback.func_230529_a_( feedback_mode );
		feedback.func_240702_b_( "!" );

		return feedback;
	}
}
