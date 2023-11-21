package com.majruszsdifficulty.gamestage;

import com.majruszlibrary.collection.CollectionHelper;
import com.majruszlibrary.events.type.ILevelEvent;
import com.majruszlibrary.events.type.IPositionEvent;
import com.majruszsdifficulty.MajruszsDifficulty;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
		return GameStageConfig.IS_PER_PLAYER_DIFFICULTY_ENABLED;
	}

	public static boolean isPerPlayerDifficultyDisabled() {
		return !GameStageConfig.IS_PER_PLAYER_DIFFICULTY_ENABLED;
	}

	public static GameStage determineGameStage( Level level, Vec3 pos ) {
		if( GameStageHelper.isPerPlayerDifficultyDisabled() ) {
			return GameStageHelper.getGlobalGameStage();
		}

		List< ? extends Player > players = level.players();
		if( players.isEmpty() ) {
			return GameStageHelper.getGlobalGameStage();
		}

		int closestPlayerIdx = 0;
		double closestPlayerDistance = players.get( 0 ).distanceToSqr( pos );
		for( int idx = 1; idx < players.size(); ++idx ) {
			double distance = players.get( idx ).distanceToSqr( pos );
			if( distance < closestPlayerDistance ) {
				closestPlayerIdx = idx;
				closestPlayerDistance = distance;
			}
		}

		return GameStageHelper.getGameStage( players.get( closestPlayerIdx ) );
	}

	public static < Type extends ILevelEvent & IPositionEvent > GameStage determineGameStage( Type data ) {
		return GameStageHelper.determineGameStage( data.getLevel(), data.getPosition() );
	}

	public static GameStage determineGameStage( Player player ) {
		return GameStageHelper.isPerPlayerDifficultyEnabled() ? GameStageHelper.getGameStage( player ) : GameStageHelper.getGlobalGameStage();
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
		return GameStageConfig.GAME_STAGES;
	}

	private GameStageHelper() {}
}
