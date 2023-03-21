package com.majruszsdifficulty.commands;

import com.majruszsdifficulty.gamestage.GameStage;
import com.mlib.annotations.AutoInstance;
import com.mlib.commands.CommandData;

@AutoInstance
public class GameStageGetCommand extends DifficultyCommand {
	public GameStageGetCommand() {
		this.newBuilder().literal( "gamestage", "gamestate" ).execute( this::handle );
	}

	private int handle( CommandData data ) {
		data.source.sendSuccess( createGameStageMessage( GameStage.getCurrentStage(), "current" ), true );

		return GameStage.getCurrentStage().ordinal();
	}
}
