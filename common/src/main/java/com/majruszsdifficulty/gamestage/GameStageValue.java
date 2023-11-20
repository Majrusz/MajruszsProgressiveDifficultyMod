package com.majruszsdifficulty.gamestage;

import com.majruszlibrary.collection.DefaultMap;

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

	public static GameStageValue< Boolean > alwaysEnabled() {
		return new GameStageValue<>( DefaultMap.of( DefaultMap.defaultEntry( true ) ) );
	}

	public static GameStageValue< Boolean > disabledOn( String... ids ) {
		DefaultMap< Boolean > map = DefaultMap.of( DefaultMap.defaultEntry( true ) );
		for( String id : ids ) {
			map.put( id, false );
		}

		return new GameStageValue<>( map );
	}

	public void set( Map< String, Type > map ) {
		this.set( DefaultMap.of( map ) );
	}

	public void set( DefaultMap< Type > map ) {
		this.map = map;
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
		this.set( map );
	}
}
