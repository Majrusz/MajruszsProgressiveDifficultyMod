package com.majruszs_difficulty.commands;

import com.majruszs_difficulty.GameState;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

/** Command responsible for changing current game state. */
public final class ChangeGameStateCommand {
	private ChangeGameStateCommand() {}

	public static void register( CommandDispatcher< CommandSourceStack > dispatcher ) {
		dispatcher.register( Commands.literal( "currentgamestate" )
			.requires( source->source.hasPermission( 0 ) )
			.executes( entity->getCurrentState( entity.getSource() ) ) );

		dispatcher.register( Commands.literal( "gamestate" )
			.requires( source->source.hasPermission( 4 ) )
			.then( Commands.literal( "normal" )
				.executes( entity->changeState( entity.getSource(), GameState.State.NORMAL ) ) )
			.then( Commands.literal( "expert" )
				.executes( entity->changeState( entity.getSource(), GameState.State.EXPERT ) ) )
			.then( Commands.literal( "master" )
				.executes( entity->changeState( entity.getSource(), GameState.State.MASTER ) ) ) );
	}

	public static int getCurrentState( CommandSourceStack source ) {
		source.sendSuccess( getFeedbackMessage( GameState.getCurrentMode(), "current" ), true );

		return GameState.convertStateToInteger( GameState.getCurrentMode() );
	}

	public static int changeState( CommandSourceStack source, GameState.State state ) {
		if( GameState.changeModeWithAdvancement( state, source.getServer() ) )
			source.sendSuccess( getFeedbackMessage( state, "change" ), true );

		return GameState.convertStateToInteger( state );
	}

	private static MutableComponent getFeedbackMessage( GameState.State state, String translationPart ) {
		MutableComponent feedback = new TranslatableComponent( "commands.game_state." + translationPart );
		MutableComponent feedback_mode = GameState.getGameStateText( state );

		feedback.append( " " );
		feedback.append( feedback_mode );
		feedback.append( "!" );

		return feedback;
	}
}
