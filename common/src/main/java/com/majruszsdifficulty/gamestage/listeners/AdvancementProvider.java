package com.majruszsdifficulty.gamestage.listeners;

import com.majruszlibrary.contexts.OnPlayerLoggedIn;
import com.majruszlibrary.contexts.base.Condition;
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
			.forEach( player->MajruszsDifficulty.GAME_STAGE_ADVANCEMENT.trigger( player, data.current ) );
	}

	private static void giveAdvancement( OnPlayerGameStageChanged data ) {
		MajruszsDifficulty.GAME_STAGE_ADVANCEMENT.trigger( ( ServerPlayer )data.player, data.current );
	}

	private static void giveAdvancement( OnPlayerLoggedIn data ) {
		MajruszsDifficulty.GAME_STAGE_ADVANCEMENT.trigger( data.player, GameStageHelper.determineGameStage( data.player ) );
	}
}
