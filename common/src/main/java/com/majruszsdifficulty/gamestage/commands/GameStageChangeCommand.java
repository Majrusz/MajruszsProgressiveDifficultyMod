package com.majruszsdifficulty.gamestage.commands;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.mlib.annotation.AutoInstance;
import com.mlib.command.Command;
import com.mlib.command.CommandData;
import com.mlib.command.IParameter;
import com.mlib.text.TextHelper;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

@AutoInstance
public class GameStageChangeCommand {
	static final IParameter< String > GAME_STAGE = Command.string()
		.named( "name" )
		.suggests( ()->GameStageHelper.getGameStages().stream().map( GameStage::getName ).toList() );

	public GameStageChangeCommand() {
		Command.create()
			.literal( "gamestage", "gamestate" )
			.hasPermission( 4 )
			.parameter( GAME_STAGE )
			.execute( this::handle )
			.register();
	}

	private int handle( CommandData data ) throws CommandSyntaxException {
		String name = data.get( GAME_STAGE );
		GameStage gameStage = GameStageHelper.getGameStages().stream().filter( stage->stage.is( name ) ).findFirst().orElseThrow();
		boolean hasGameStageChanged = GameStageHelper.setGameStage( gameStage );
		data.source.sendSuccess( ()->TextHelper.translatable( "commands.gamestage.%s".formatted( hasGameStageChanged ? "changed" : "cannot_change" ), gameStage.getComponent() ), true );

		return 0;
	}
}
