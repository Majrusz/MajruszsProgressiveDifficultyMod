package com.majruszsdifficulty.config;

import com.mlib.config.DoubleConfig;

public class GameStageDoubleConfig extends GameStageConfig< Double > {
	public GameStageDoubleConfig( String name, String comment, double defaultNormal, double defaultExpert, double defaultMaster, double min, double max ) {
		super( name, comment, normalMode( defaultNormal, min, max ), expertMode( defaultExpert, min, max ), masterMode( defaultMaster, min, max ) );
	}

	private static DoubleConfig normalMode( double value, double min, double max ) {
		return new DoubleConfig( "normal", "Normal Mode", false, value, min, max );
	}

	private static DoubleConfig expertMode( double value, double min, double max ) {
		return new DoubleConfig( "expert", "Expert Mode", false, value, min, max );
	}

	private static DoubleConfig masterMode( double value, double min, double max ) {
		return new DoubleConfig( "master", "Master Mode", false, value, min, max );
	}
}
