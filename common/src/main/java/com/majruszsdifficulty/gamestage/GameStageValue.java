package com.majruszsdifficulty.gamestage;

import com.mlib.collection.DefaultMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameStageValue< Type > {
	private Map< String, Type > map;
	private List< Type > list;

	@SafeVarargs
	public static < Type > GameStageValue< Type > of( DefaultMap.Entry< String, Type >... entries ) {
		return new GameStageValue<>( DefaultMap.of( entries ) );
	}

	public void set( Map< String, Type > map ) {
		this.map = DefaultMap.of( map );
		this.list = new ArrayList<>();
		for( GameStage gameStage : GameStageHelper.getGameStages() ) {
			this.list.add( this.map.get( gameStage.getId() ) );
		}
	}

	public Map< String, Type > get() {
		return this.map;
	}

	public Type get( GameStage gameStage ) {
		return this.list.get( gameStage.getOrdinal() );
	}

	private GameStageValue( DefaultMap< Type > map ) {
		this.map = map;
	}
}
