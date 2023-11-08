package com.majruszsdifficulty.gamestage;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.mlib.collection.CollectionHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Map;

public class GameStageHelper {
	public static boolean setGameStage( GameStage gameStage, Player player ) {
		return MajruszsDifficulty.WORLD_DATA.setGameStage( gameStage, player );
	}

	public static boolean setGlobalGameStage( GameStage gameStage ) {
		return MajruszsDifficulty.WORLD_DATA.setGlobalGameStage( gameStage );
	}

	public static boolean increaseGameStage( GameStage gameStage, Player player ) {
		GameStage playerStage = GameStageHelper.getGameStage( player );

		return playerStage.getOrdinal() < gameStage.getOrdinal()
			&& GameStageHelper.setGameStage( gameStage, player );
	}

	public static boolean increaseGlobalGameStage( GameStage gameStage ) {
		GameStage globalStage = GameStageHelper.getGlobalGameStage();

		return globalStage.getOrdinal() < gameStage.getOrdinal()
			&& GameStageHelper.setGlobalGameStage( gameStage );
	}

	public static GameStage find( String id ) {
		return GameStageHelper.getGameStages().stream().filter( stage->stage.is( id ) ).findFirst().orElse( GameStageHelper.getDefaultGameStage() );
	}

	public static Map< String, GameStage > mapToGameStages( Map< String, String > names ) {
		return CollectionHelper.map( names, GameStageHelper::find, Object2ObjectOpenHashMap::new );
	}

	public static Map< String, String > mapToNames( Map< String, GameStage > gameStages ) {
		return CollectionHelper.map( gameStages, GameStage::getId, Object2ObjectOpenHashMap::new );
	}

	public static boolean isPerPlayerDifficultyEnabled() {
		return MajruszsDifficulty.CONFIG.isPerPlayerDifficultyEnabled;
	}

	public static boolean isPerPlayerDifficultyDisabled() {
		return !MajruszsDifficulty.CONFIG.isPerPlayerDifficultyEnabled;
	}

	public static GameStage getGameStage( Player player ) {
		return MajruszsDifficulty.WORLD_DATA.getGameStage( player );
	}

	public static GameStage getGlobalGameStage() {
		return MajruszsDifficulty.WORLD_DATA.getGlobalGameStage();
	}

	public static GameStage getDefaultGameStage() {
		return GameStageHelper.getGameStages().get( 0 );
	}

	public static List< GameStage > getGameStages() {
		return MajruszsDifficulty.CONFIG.gameStages;
	}

	private GameStageHelper() {}
}
