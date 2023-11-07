package com.majruszsdifficulty.data;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.mlib.data.Serializables;
import com.mlib.entity.EntityHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class WorldData extends com.mlib.data.WorldData {
	private GameStage gameStage = GameStage.NORMAL;
	private Map< String, GameStage > playerGameStages = new Object2ObjectOpenHashMap<>();

	static {
		Serializables.get( WorldData.class )
			.defineString( "current_game_stage", s->s.gameStage.getName(), ( s, v )->s.gameStage = GameStageHelper.find( v ) )
			.defineStringMap( "player_game_stages", s->GameStageHelper.mapToNames( s.playerGameStages ), ( s, v )->s.playerGameStages = GameStageHelper.mapToGameStages( v ) );
	}

	public boolean setGameStage( GameStage gameStage, @Nullable Player player ) {
		if( player != null ) {
			String uuid = EntityHelper.getPlayerUUID( player );
			if( !this.playerGameStages.computeIfAbsent( uuid, key->GameStage.NORMAL ).equals( gameStage ) ) {
				this.playerGameStages.put( uuid, gameStage );
				this.setDirty();

				return true;
			}
		} else {
			if( !this.gameStage.equals( gameStage ) ) {
				this.gameStage = gameStage;
				this.setDirty();

				return true;
			}
		}

		return false;
	}

	public GameStage getGameStage( @Nullable Player player ) {
		if( player != null ) {
			GameStage gameStage = this.playerGameStages.get( EntityHelper.getPlayerUUID( player ) );
			if( gameStage != null ) {
				return gameStage;
			}
		}

		return this.gameStage;
	}

	@Override
	protected void setupDefaultValues() {
		super.setupDefaultValues();

		this.gameStage = GameStage.NORMAL;
		this.playerGameStages = new Object2ObjectOpenHashMap<>();
	}
}
