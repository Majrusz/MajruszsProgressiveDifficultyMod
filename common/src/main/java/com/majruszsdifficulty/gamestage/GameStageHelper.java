package com.majruszsdifficulty.gamestage;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.mlib.collection.CollectionHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class GameStageHelper {
	public static boolean setGameStage( GameStage gameStage, @Nullable Player player ) {
		return MajruszsDifficulty.WORLD_DATA.setGameStage( gameStage, player );
	}

	public static boolean setGameStage( GameStage gameStage ) {
		return GameStageHelper.setGameStage( gameStage, null );
	}

	public static GameStage find( String id ) {
		return GameStageHelper.getGameStages().stream().filter( stage->stage.is( id ) ).findFirst().orElse( GameStage.NORMAL );
	}

	public static Map< String, GameStage > mapToGameStages( Map< String, String > names ) {
		return CollectionHelper.map( names, GameStageHelper::find, Object2ObjectOpenHashMap::new );
	}

	public static Map< String, String > mapToNames( Map< String, GameStage > gameStages ) {
		return CollectionHelper.map( gameStages, GameStage::getName, Object2ObjectOpenHashMap::new );
	}

	public static boolean isPerPlayerDifficultyEnabled() {
		return MajruszsDifficulty.CONFIG.isPerPlayerDifficultyEnabled;
	}

	public static GameStage getGameStage( @Nullable Player player ) {
		return MajruszsDifficulty.WORLD_DATA.getGameStage( player );
	}

	public static GameStage getGameStage() {
		return GameStageHelper.getGameStage( null );
	}

	public static List< GameStage > getGameStages() {
		return MajruszsDifficulty.CONFIG.gameStages;
	}

	private GameStageHelper() {}
}
