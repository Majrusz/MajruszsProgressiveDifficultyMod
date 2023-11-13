package com.majruszsdifficulty.gamestage.listeners;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.contexts.OnGlobalGameStageChanged;
import com.majruszsdifficulty.contexts.OnPlayerGameStageChanged;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnPlayerLoggedIn;
import com.mlib.contexts.base.Condition;
import com.mlib.platform.Side;
import net.minecraft.server.level.ServerPlayer;

@AutoInstance
public class AdvancementProvider {
	public AdvancementProvider() {
		OnGlobalGameStageChanged.listen( this::giveAdvancement )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( GameStageHelper::isPerPlayerDifficultyDisabled )
			.addCondition( data->data.current.getOrdinal() > data.previous.getOrdinal() );

		OnPlayerGameStageChanged.listen( this::giveAdvancement )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->data.current.getOrdinal() > data.previous.getOrdinal() );

		OnPlayerLoggedIn.listen( this::giveAdvancement )
			.addCondition( Condition.isLogicalServer() );
	}

	private void giveAdvancement( OnGlobalGameStageChanged data ) {
		Side.getServer()
			.getPlayerList()
			.getPlayers()
			.forEach( player->MajruszsDifficulty.GAME_STAGE_ADVANCEMENT.trigger( player, data.current ) );
	}

	private void giveAdvancement( OnPlayerGameStageChanged data ) {
		MajruszsDifficulty.GAME_STAGE_ADVANCEMENT.trigger( ( ServerPlayer )data.player, data.current );
	}

	private void giveAdvancement( OnPlayerLoggedIn data ) {
		MajruszsDifficulty.GAME_STAGE_ADVANCEMENT.trigger( data.player, GameStageHelper.determineGameStage( data.player ) );
	}
}
