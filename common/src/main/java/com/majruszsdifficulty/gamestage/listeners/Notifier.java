package com.majruszsdifficulty.gamestage.listeners;

import com.majruszlibrary.contexts.base.Condition;
import com.majruszlibrary.platform.Side;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.contexts.OnGlobalGameStageChanged;
import com.majruszsdifficulty.gamestage.contexts.OnPlayerGameStageChanged;
import net.minecraft.world.entity.player.Player;

public class Notifier {
	static {
		OnGlobalGameStageChanged.listen( Notifier::notify )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( GameStageHelper::isPerPlayerDifficultyDisabled )
			.addCondition( data->data.current.getOrdinal() > data.previous.getOrdinal() );

		OnPlayerGameStageChanged.listen( Notifier::notify )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->data.current.getOrdinal() > data.previous.getOrdinal() );
	}

	private static void notify( OnGlobalGameStageChanged data ) {
		Side.getServer()
			.getPlayerList()
			.getPlayers()
			.forEach( player->Notifier.send( player, data.current ) );
	}

	private static void notify( OnPlayerGameStageChanged data ) {
		Notifier.send( data.player, data.current );
	}

	private static void send( Player player, GameStage gameStage ) {
		gameStage.getMessages().forEach( message->player.displayClientMessage( message, false ) );
	}
}
