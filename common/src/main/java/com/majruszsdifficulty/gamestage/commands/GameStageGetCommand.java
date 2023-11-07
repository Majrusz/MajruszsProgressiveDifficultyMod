package com.majruszsdifficulty.gamestage.commands;

import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.mlib.annotation.AutoInstance;
import com.mlib.command.Command;
import com.mlib.command.CommandData;
import com.mlib.text.TextHelper;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

@AutoInstance
public class GameStageGetCommand {
	public GameStageGetCommand() {
		Command.create()
			.literal( "gamestage", "gamestate" )
			.hasPermission( 4 )
			.execute( this::handle )
			.register();
	}

	private int handle( CommandData data ) throws CommandSyntaxException {
		data.source.sendSuccess( ()->TextHelper.translatable( "commands.gamestage.current", GameStageHelper.getGameStage().getComponent() ), true );

		return 0;
	}
}
