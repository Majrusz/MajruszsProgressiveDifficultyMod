package com.majruszsdifficulty.commands;

import com.majruszsdifficulty.GameStage;
import com.mlib.commands.BaseCommand;
import com.mlib.commands.CommandManager;
import com.mlib.commands.IRegistrableCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

/** Command that changes current game stage globally. */
public class ChangeGameStageCommand extends BaseCommand implements IRegistrableCommand {
	private static final String ENUM_ARGUMENT_NAME = "gamestage";
	private static final RequiredArgumentBuilder< CommandSourceStack, ? > ENUM_ARGUMENT = enumArgument( ENUM_ARGUMENT_NAME, GameStage.Stage.class );

	/** Registers this command. */
	@Override
	public void register( CommandDispatcher< CommandSourceStack > commandDispatcher ) {
		CommandManager commandManager = new CommandManager( commandDispatcher );
		commandManager.register( literal( "gamestage" ), ENUM_ARGUMENT, hasPermission( 4 ), this::handleCommand );
		commandManager.register( literal( "gamestate" ), ENUM_ARGUMENT, hasPermission( 4 ), this::handleCommand );
	}

	/** Changes current game stage and sends information to all players. */
	protected int handleCommand( CommandContext< CommandSourceStack > context, CommandSourceStack source ) {
		GameStage.Stage gameStage = getEnum( context, ENUM_ARGUMENT_NAME, GameStage.Stage.class );
		if( GameStage.changeModeWithAdvancement( gameStage, source.getServer() ) ) {
			source.sendSuccess( CommandsHelper.createGameStageMessage( gameStage, "changed" ), true );
		} else {
			source.sendSuccess( CommandsHelper.createGameStageMessage( gameStage, "cannotchange" ), true );
		}

		return GameStage.convertStageToInteger( gameStage );
	}
}
