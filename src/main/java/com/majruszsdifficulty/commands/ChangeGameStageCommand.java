package com.majruszsdifficulty.commands;

import com.majruszsdifficulty.GameStage;
import com.mlib.annotations.AutoInstance;
import com.mlib.commands.CommandData;

@AutoInstance
public class ChangeGameStageCommand extends DifficultyCommand {
	public ChangeGameStageCommand() {
		this.newBuilder()
			.literal( "gamestage", "gamestate" )
			.enumeration( "gamestage", GameStage.Stage.class )
			.hasPermission( 4 )
			.execute( this::handle );
	}

	private int handle( CommandData data ) {
		GameStage.Stage gameStage = this.getEnumeration( data, "gamestage", GameStage.Stage.class );
		boolean hasGameStageChanged = GameStage.changeModeWithAdvancement( gameStage, data.source.getServer() );
		String translationKey = hasGameStageChanged ? "changed" : "cannot_change";
		data.source.sendSuccess( this.createGameStageMessage( gameStage, translationKey ), true );

		return GameStage.convertStageToInteger( gameStage );
	}
}
