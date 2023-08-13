package com.majruszsdifficulty.gamestage.commands;

import com.majruszsdifficulty.gamestage.GameStage;
import com.mlib.modhelper.AutoInstance;
import com.mlib.commands.Command;
import com.mlib.commands.CommandData;
import net.minecraft.network.chat.Component;

@AutoInstance
public class GameStageGetCommand extends Command {
	public GameStageGetCommand() {
		this.newBuilder()
			.literal( "gamestage", "gamestate" )
			.execute( this::handle );
	}

	private int handle( CommandData data ) {
		data.source.sendSuccess( Component.translatable( "commands.gamestage.current", GameStage.getGameStageText( GameStage.getCurrentStage() ) ), true );

		return GameStage.getCurrentStage().ordinal();
	}
}
