package com.majruszsdifficulty.config;

import com.mlib.config.IntegerConfig;

public class GameStageIntegerConfig extends GameStageConfig< Integer > {
	public GameStageIntegerConfig( String name, String comment, int defaultNormal, int defaultExpert, int defaultMaster, int min, int max ) {
		super( name, comment, normalMode( defaultNormal, min, max ), expertMode( defaultExpert, min, max ), masterMode( defaultMaster, min, max ) );
	}

	private static IntegerConfig normalMode( int value, int min, int max ) {
		return new IntegerConfig( "normal", "Normal Mode", false, value, min, max );
	}

	private static IntegerConfig expertMode( int value, int min, int max ) {
		return new IntegerConfig( "expert", "Expert Mode", false, value, min, max );
	}

	private static IntegerConfig masterMode( int value, int min, int max ) {
		return new IntegerConfig( "master", "Master Mode", false, value, min, max );
	}
}