package com.majruszsdifficulty.commands;

import com.majruszsdifficulty.GameStage;
import com.mlib.annotations.AutoInstance;
import com.mlib.commands.CommandData;

@AutoInstance
public class GetGameStageCommand extends DifficultyCommand {
	public GetGameStageCommand() {
		this.newBuilder().literal( "gamestage", "gamestate" ).execute( this::handle );
	}

	private int handle( CommandData data ) {
		data.source.sendSuccess( this.createGameStageMessage( GameStage.getCurrentStage(), "current" ), true );

		return GameStage.convertStageToInteger( GameStage.getCurrentStage() );
	}
}
