package com.majruszsdifficulty.data;

import com.majruszsdifficulty.gamestage.GameStage;
import com.mlib.data.Serializables;

import java.util.List;

public class Config extends com.mlib.data.Config {
	public List< GameStage > gameStages = List.of( GameStage.NORMAL, GameStage.EXPERT, GameStage.MASTER );
	public boolean isPerPlayerDifficultyEnabled = false;

	public Config( String name ) {
		super( name );

		Serializables.get( Config.class )
			.defineCustomList( "game_stages", s->s.gameStages, ( s, v )->s.gameStages = Config.validate( v ), GameStage::new )
			.defineBoolean( "is_per_player_difficulty_enabled", s->s.isPerPlayerDifficultyEnabled, ( s, v )->s.isPerPlayerDifficultyEnabled = v );
	}

	private static List< GameStage > validate( List< GameStage > gameStages ) {
		if( !gameStages.containsAll( List.of( GameStage.NORMAL, GameStage.EXPERT, GameStage.MASTER ) ) ) {
			throw new IllegalArgumentException( "Default game stages cannot be removed" );
		}

		return gameStages;
	}
}
