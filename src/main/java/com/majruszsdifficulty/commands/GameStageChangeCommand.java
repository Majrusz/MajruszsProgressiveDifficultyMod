package com.majruszsdifficulty.commands;

import com.majruszsdifficulty.GameStage;
import com.mlib.annotations.AutoInstance;
import com.mlib.commands.CommandData;

@AutoInstance
public class GameStageChangeCommand extends DifficultyCommand {
	public GameStageChangeCommand() {
		this.newBuilder()
			.literal( "gamestage", "gamestate" )
			.enumeration( GameStage.class )
			.hasPermission( 4 )
			.execute( this::handle );
	}

	private int handle( CommandData data ) {
		GameStage gameStage = this.getEnumeration( data, GameStage.class );
		boolean hasGameStageChanged = GameStage.changeStage( gameStage, data.source.getServer() );
		String translationKey = hasGameStageChanged ? "changed" : "cannot_change";
		data.source.sendSuccess( createGameStageMessage( gameStage, translationKey ), true );

		return gameStage.ordinal();
	}
}
