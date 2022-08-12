package com.majruszsdifficulty.config;

import com.mlib.config.StringListConfig;

import java.util.List;

public class GameStageStringListConfig extends GameStageConfig< List< ? extends String > > {
	public GameStageStringListConfig( String name, String comment, String[] defaultNormal, String[] defaultExpert, String[] defaultMaster ) {
		super( name, comment, normalMode( defaultNormal ), expertMode( defaultExpert ), masterMode( defaultMaster ) );
	}

	private static StringListConfig normalMode( String[] values ) {
		return new StringListConfig( "normal", "Normal Mode", false, values );
	}

	private static StringListConfig expertMode( String[] values ) {
		return new StringListConfig( "expert", "Expert Mode", false, values );
	}

	private static StringListConfig masterMode( String[] values ) {
		return new StringListConfig( "master", "Master Mode", false, values );
	}
}
