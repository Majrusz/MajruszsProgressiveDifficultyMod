package com.majruszsdifficulty.data;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.mlib.data.Serializables;

public class WorldData extends com.mlib.data.WorldData {
	private GameStage gameStage = GameStage.NORMAL;

	static {
		Serializables.get( WorldData.class )
			.defineString( "current_game_stage", s->s.gameStage.getName(), ( s, v )->s.gameStage = GameStageHelper.find( v ) );
	}

	public boolean setGameStage( GameStage gameStage ) {
		if( !this.gameStage.equals( gameStage ) ) {
			this.gameStage = gameStage;
			this.setDirty();

			return true;
		}

		return false;
	}

	public GameStage getGameStage() {
		return this.gameStage;
	}

	@Override
	protected void setupDefaultValues() {
		super.setupDefaultValues();

		this.gameStage = GameStage.NORMAL;
	}
}
