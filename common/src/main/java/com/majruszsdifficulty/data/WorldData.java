package com.majruszsdifficulty.data;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.events.base.Events;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.bloodmoon.BloodMoon;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.contexts.OnGlobalGameStageChanged;
import com.majruszsdifficulty.gamestage.contexts.OnPlayerGameStageChanged;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class WorldData {
	private static GameStage GAME_STAGE = GameStageHelper.getDefaultGameStage();
	private static Map< String, GameStage > PLAYER_GAME_STAGES = new Object2ObjectOpenHashMap<>();
	private static BloodMoon BLOOD_MOON = new BloodMoon();

	static {
		Serializables.getStatic( WorldData.class )
			.define( "global_game_stage", Reader.string(), ()->GAME_STAGE.getId(), v->GAME_STAGE = GameStageHelper.find( v ) )
			.define( "player_game_stages", Reader.map( Reader.string() ), ()->GameStageHelper.mapToNames( PLAYER_GAME_STAGES ), v->PLAYER_GAME_STAGES = GameStageHelper.mapToGameStages( v ) )
			.define( "blood_moon", Reader.custom( BloodMoon::new ), ()->BLOOD_MOON, v->BLOOD_MOON = v );

		Serializables.getStatic( WorldData.Client.class )
			.define( "global_game_stage", Reader.string(), ()->GAME_STAGE.getId(), v->GAME_STAGE = GameStageHelper.find( v ) )
			.define( "blood_moon", Reader.bool(), ()->BLOOD_MOON.isActive(), v->BLOOD_MOON.setActive( v ) );
	}

	public static void setDirty() {
		MajruszsDifficulty.WORLD_DATA.setDirty();
	}

	public static boolean setGameStage( GameStage gameStage, Player player ) {
		String uuid = EntityHelper.getPlayerUUID( player );
		if( !PLAYER_GAME_STAGES.computeIfAbsent( uuid, key->GameStageHelper.getDefaultGameStage() ).equals( gameStage ) ) {
			GameStage previous = PLAYER_GAME_STAGES.get( uuid );
			PLAYER_GAME_STAGES.put( uuid, gameStage );
			WorldData.setDirty();
			Events.dispatch( new OnPlayerGameStageChanged( previous, gameStage, player ) );

			return true;
		}

		return false;
	}

	public static boolean setGlobalGameStage( GameStage gameStage ) {
		if( !GAME_STAGE.equals( gameStage ) ) {
			GameStage previous = GAME_STAGE;
			GAME_STAGE = gameStage;
			WorldData.setDirty();
			Events.dispatch( new OnGlobalGameStageChanged( previous, gameStage ) );

			return true;
		}

		return false;
	}

	public static GameStage getGameStage( Player player ) {
		GameStage gameStage = PLAYER_GAME_STAGES.get( EntityHelper.getPlayerUUID( player ) );
		if( gameStage != null ) {
			return gameStage;
		}

		return WorldData.getGlobalGameStage();
	}

	public static GameStage getGlobalGameStage() {
		return GAME_STAGE;
	}

	public static BloodMoon getBloodMoon() {
		return BLOOD_MOON;
	}

	public static void setupDefaultValues() {
		GAME_STAGE = GameStageHelper.getDefaultGameStage();
		PLAYER_GAME_STAGES = new Object2ObjectOpenHashMap<>();
		BLOOD_MOON = new BloodMoon();
	}

	public static class Client {}
}
