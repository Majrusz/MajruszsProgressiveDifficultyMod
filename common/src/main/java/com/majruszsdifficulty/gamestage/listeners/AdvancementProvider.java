package com.majruszsdifficulty.gamestage.listeners;

import com.majruszlibrary.events.OnPlayerLoggedIn;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.platform.Side;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.contexts.OnGlobalGameStageChanged;
import com.majruszsdifficulty.gamestage.contexts.OnPlayerGameStageChanged;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementProvider {
	static {
		OnGlobalGameStageChanged.listen( AdvancementProvider::giveAdvancement )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( GameStageHelper::isPerPlayerDifficultyDisabled )
			.addCondition( data->data.current.getOrdinal() > data.previous.getOrdinal() );

		OnPlayerGameStageChanged.listen( AdvancementProvider::giveAdvancement )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->data.current.getOrdinal() > data.previous.getOrdinal() );

		OnPlayerLoggedIn.listen( AdvancementProvider::giveAdvancement )
			.addCondition( Condition.isLogicalServer() );
	}

	private static void giveAdvancement( OnGlobalGameStageChanged data ) {
		Side.getServer()
			.getPlayerList()
			.getPlayers()
			.forEach( player->MajruszsDifficulty.Advancements.GAME_STAGE.trigger( player, data.current ) );
	}

	private static void giveAdvancement( OnPlayerGameStageChanged data ) {
		MajruszsDifficulty.Advancements.GAME_STAGE.trigger( ( ServerPlayer )data.player, data.current );
	}

	private static void giveAdvancement( OnPlayerLoggedIn data ) {
		MajruszsDifficulty.Advancements.GAME_STAGE.trigger( data.player, GameStageHelper.determineGameStage( data.player ) );
	}
}
