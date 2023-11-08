package com.majruszsdifficulty.gamestage.listeners;

import com.majruszsdifficulty.contexts.OnGlobalGameStageChanged;
import com.majruszsdifficulty.contexts.OnPlayerGameStageChanged;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.base.Condition;
import com.mlib.platform.Side;
import net.minecraft.world.entity.player.Player;

@AutoInstance
public class Notifier {
	public Notifier() {
		OnGlobalGameStageChanged.listen( this::notify )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->!GameStageHelper.isPerPlayerDifficultyEnabled() )
			.addCondition( data->data.current.getOrdinal() > data.previous.getOrdinal() );

		OnPlayerGameStageChanged.listen( this::notify )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->data.current.getOrdinal() > data.previous.getOrdinal() );
	}

	private void notify( OnGlobalGameStageChanged data ) {
		Side.getServer()
			.getPlayerList()
			.getPlayers()
			.forEach( player->Notifier.send( player, data.current ) );
	}

	private void notify( OnPlayerGameStageChanged data ) {
		Notifier.send( data.player, data.current );
	}

	private static void send( Player player, GameStage gameStage ) {
		gameStage.getMessages().forEach( message->player.displayClientMessage( message, false ) );
	}
}
