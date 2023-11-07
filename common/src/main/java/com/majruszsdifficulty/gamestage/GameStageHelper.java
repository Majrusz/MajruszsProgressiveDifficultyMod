package com.majruszsdifficulty.gamestage;

import com.majruszsdifficulty.MajruszsDifficulty;

import java.util.List;

public class GameStageHelper {
	public static boolean setGameStage( GameStage gameStage ) {
		return MajruszsDifficulty.WORLD_DATA.setGameStage( gameStage );
	}

	public static GameStage find( String id ) {
		return GameStageHelper.getGameStages().stream().filter( stage->stage.is( id ) ).findFirst().orElse( GameStage.NORMAL );
	}

	public static GameStage getGameStage() {
		return MajruszsDifficulty.WORLD_DATA.getGameStage();
	}

	public static List< GameStage > getGameStages() {
		return MajruszsDifficulty.CONFIG.gameStages;
	}

	private GameStageHelper() {}
}
