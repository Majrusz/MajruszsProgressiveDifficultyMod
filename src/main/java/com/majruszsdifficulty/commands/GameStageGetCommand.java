package com.majruszsdifficulty.commands;

import com.majruszsdifficulty.GameStage;
import com.mlib.annotations.AutoInstance;
import com.mlib.commands.CommandData;

@AutoInstance
public class GameStageGetCommand extends DifficultyCommand {
	public GameStageGetCommand() {
		this.newBuilder().literal( "gamestage", "gamestate" ).execute( this::handle );
	}

	private int handle( CommandData data ) {
		data.source.sendSuccess( this.createGameStageMessage( GameStage.getCurrentStage(), "current" ), true );

		return GameStage.convertStageToInteger( GameStage.getCurrentStage() );
	}
}
