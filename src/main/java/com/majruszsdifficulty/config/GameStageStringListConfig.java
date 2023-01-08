package com.majruszsdifficulty.config;

import com.mlib.config.StringListConfig;

import java.util.List;

public class GameStageStringListConfig extends GameStageConfig< List< ? extends String > > {
	public GameStageStringListConfig( String[] defaultNormal, String[] defaultExpert, String[] defaultMaster ) {
		super( new NormalConfig( defaultNormal ), new ExpertConfig( defaultExpert ), new MasterConfig( defaultMaster ) );
	}

	static class NormalConfig extends StringListConfig {
		public NormalConfig( String[] defaultValue ) {
			super( defaultValue );

			this.name( "normal" ).comment( "Normal Mode" );
		}
	}

	static class ExpertConfig extends StringListConfig {
		public ExpertConfig( String[] defaultValue ) {
			super( defaultValue );

			this.name( "expert" ).comment( "Expert Mode" );
		}
	}

	static class MasterConfig extends StringListConfig {
		public MasterConfig( String[] defaultValue ) {
			super( defaultValue );

			this.name( "master" ).comment( "Master Mode" );
		}
	}
}
