package com.majruszsdifficulty.data;

import com.majruszsdifficulty.contexts.OnGlobalGameStageChanged;
import com.majruszsdifficulty.contexts.OnPlayerGameStageChanged;
import com.majruszsdifficulty.events.bloodmoon.BloodMoon;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.mlib.contexts.base.Contexts;
import com.mlib.data.Serializables;
import com.mlib.entity.EntityHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class WorldData extends com.mlib.data.WorldData {
	private GameStage gameStage = GameStageHelper.getDefaultGameStage();
	private Map< String, GameStage > playerGameStages = new Object2ObjectOpenHashMap<>();
	private BloodMoon bloodMoon = new BloodMoon();

	static {
		Serializables.get( WorldData.class )
			.defineString( "current_game_stage", s->s.gameStage.getId(), ( s, v )->s.gameStage = GameStageHelper.find( v ) )
			.defineStringMap( "player_game_stages", s->GameStageHelper.mapToNames( s.playerGameStages ), ( s, v )->s.playerGameStages = GameStageHelper.mapToGameStages( v ) )
			.defineCustom( "blood_moon", s->s.bloodMoon, ( s, v )->s.bloodMoon = v, BloodMoon::new );
	}

	public boolean setGameStage( GameStage gameStage, Player player ) {
		String uuid = EntityHelper.getPlayerUUID( player );
		if( !this.playerGameStages.computeIfAbsent( uuid, key->GameStageHelper.getDefaultGameStage() ).equals( gameStage ) ) {
			GameStage previous = this.playerGameStages.get( uuid );
			this.playerGameStages.put( uuid, gameStage );
			this.setDirty();
			Contexts.dispatch( new OnPlayerGameStageChanged( previous, gameStage, player ) );

			return true;
		}

		return false;
	}

	public boolean setGlobalGameStage( GameStage gameStage ) {
		if( !this.gameStage.equals( gameStage ) ) {
			GameStage previous = this.gameStage;
			this.gameStage = gameStage;
			this.setDirty();
			Contexts.dispatch( new OnGlobalGameStageChanged( previous, gameStage ) );

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

	public BloodMoon getBloodMoon() {
		return this.bloodMoon;
	}

	@Override
	protected void setupDefaultValues() {
		super.setupDefaultValues();

		this.gameStage = GameStageHelper.getDefaultGameStage();
		this.playerGameStages = new Object2ObjectOpenHashMap<>();
	}
}
