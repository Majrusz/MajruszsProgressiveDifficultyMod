package com.majruszsdifficulty.gamemodifiers.list.gamestages;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.contexts.OnGameStageChange;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;

@AutoInstance
public class AdvancementProvider extends GameModifier {
	public AdvancementProvider() {
		super( Registries.Modifiers.DEFAULT );

		OnGameStageChange.listen( this::giveAdvancement )
			.addCondition( Condition.predicate( data->!data.isLoadedFromDisk() ) )
			.addCondition( Condition.predicate( data->data.previous.ordinal() < data.current.ordinal() ) )
			.insertTo( this );
	}

	private void giveAdvancement( OnGameStageChange.Data data ) {
		data.server.getPlayerList()
			.getPlayers()
			.forEach( player->Registries.GAME_STATE_TRIGGER.trigger( player, data.current ) );
	}
}
