package com.majruszsdifficulty.data;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.mlib.data.Serializables;
import com.mlib.entity.EntityHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class WorldData extends com.mlib.data.WorldData {
	private GameStage gameStage = GameStageHelper.getDefaultGameStage();
	private Map< String, GameStage > playerGameStages = new Object2ObjectOpenHashMap<>();

	static {
		Serializables.get( WorldData.class )
			.defineString( "current_game_stage", s->s.gameStage.getName(), ( s, v )->s.gameStage = GameStageHelper.find( v ) )
			.defineStringMap( "player_game_stages", s->GameStageHelper.mapToNames( s.playerGameStages ), ( s, v )->s.playerGameStages = GameStageHelper.mapToGameStages( v ) );
	}

	public boolean setGameStage( GameStage gameStage, Player player ) {
		String uuid = EntityHelper.getPlayerUUID( player );
		if( !this.playerGameStages.computeIfAbsent( uuid, key->GameStageHelper.getDefaultGameStage() ).equals( gameStage ) ) {
			this.playerGameStages.put( uuid, gameStage );
			this.setDirty();

			return true;
		}

		return false;
	}

	public boolean setGlobalGameStage( GameStage gameStage ) {
		if( !this.gameStage.equals( gameStage ) ) {
			this.gameStage = gameStage;
			this.setDirty();

			return true;
		}

		return false;
	}

	public GameStage getGameStage( Player player ) {
		GameStage gameStage = this.playerGameStages.get( EntityHelper.getPlayerUUID( player ) );
		if( gameStage != null ) {
			return gameStage;
		}

		return this.getGlobalGameStage();
	}

	public GameStage getGlobalGameStage() {
		return this.gameStage;
	}

	@Override
	protected void setupDefaultValues() {
		super.setupDefaultValues();

		this.gameStage = GameStageHelper.getDefaultGameStage();
		this.playerGameStages = new Object2ObjectOpenHashMap<>();
	}
}
