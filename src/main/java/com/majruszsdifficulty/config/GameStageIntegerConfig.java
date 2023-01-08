package com.majruszsdifficulty.config;

import com.mlib.config.IntegerConfig;
import com.mlib.math.Range;

public class GameStageIntegerConfig extends GameStageConfig< Integer > {
	public GameStageIntegerConfig( int defaultNormal, int defaultExpert, int defaultMaster, Range< Integer > range ) {
		super( new NormalConfig( defaultNormal, range ), new ExpertConfig( defaultExpert, range ), new MasterConfig( defaultMaster, range ) );
	}

	static class NormalConfig extends IntegerConfig {
		public NormalConfig( int defaultValue, Range< Integer > range ) {
			super( defaultValue, range );

			this.name( "normal" ).comment( "Normal Mode" );
		}
	}

	static class ExpertConfig extends IntegerConfig {
		public ExpertConfig( int defaultValue, Range< Integer > range ) {
			super( defaultValue, range );

			this.name( "expert" ).comment( "Expert Mode" );
		}
	}

	static class MasterConfig extends IntegerConfig {
		public MasterConfig( int defaultValue, Range< Integer > range ) {
			super( defaultValue, range );

			this.name( "master" ).comment( "Master Mode" );
		}
	}
}
