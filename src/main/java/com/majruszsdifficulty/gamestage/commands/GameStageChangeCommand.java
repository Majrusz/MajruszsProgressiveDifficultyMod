package com.majruszsdifficulty.gamestage.commands;

import com.majruszsdifficulty.gamestage.GameStage;
import com.mlib.modhelper.AutoInstance;
import com.mlib.commands.Command;
import com.mlib.commands.CommandData;
import net.minecraft.network.chat.Component;

@AutoInstance
public class GameStageChangeCommand extends Command {
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
		String textId = String.format( "commands.gamestage.%s", hasGameStageChanged ? "changed" : "cannot_change" );
		data.source.sendSuccess( ()->Component.translatable( textId, GameStage.getGameStageText( GameStage.getCurrentStage() ) ), true );

		return gameStage.ordinal();
	}
}
