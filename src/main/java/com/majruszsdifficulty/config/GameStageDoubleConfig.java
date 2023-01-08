package com.majruszsdifficulty.config;

import com.mlib.config.DoubleConfig;
import com.mlib.math.Range;

public class GameStageDoubleConfig extends GameStageConfig< Double > {
	public GameStageDoubleConfig( double defaultNormal, double defaultExpert, double defaultMaster, Range< Double > range ) {
		super( new NormalConfig( defaultNormal, range ), new ExpertConfig( defaultExpert, range ), new MasterConfig( defaultMaster, range ) );
	}

	static class NormalConfig extends DoubleConfig {
		public NormalConfig( double defaultValue, Range< Double > range ) {
			super( defaultValue, range );

			this.name( "normal" ).comment( "Normal Mode" );
		}
	}

	static class ExpertConfig extends DoubleConfig {
		public ExpertConfig( double defaultValue, Range< Double > range ) {
			super( defaultValue, range );

			this.name( "expert" ).comment( "Expert Mode" );
		}
	}

	static class MasterConfig extends DoubleConfig {
		public MasterConfig( double defaultValue, Range< Double > range ) {
			super( defaultValue, range );

			this.name( "master" ).comment( "Master Mode" );
		}
	}
}
