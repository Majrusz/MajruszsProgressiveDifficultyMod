package com.majruszsdifficulty.gamestage.listeners;

import com.majruszlibrary.collection.DefaultMap;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.events.OnClampedRegionalDifficultyGet;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageConfig;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;

public class ClampedRegionalDifficultyIncreaser {
	private static GameStageValue< Float > PENALTY = GameStageValue.of(
		DefaultMap.defaultEntry( 0.0f ),
		DefaultMap.entry( GameStage.EXPERT_ID, 0.15f ),
		DefaultMap.entry( GameStage.MASTER_ID, 0.3f )
	);

	static {
		OnClampedRegionalDifficultyGet.listen( ClampedRegionalDifficultyIncreaser::increase );

		Serializables.getStatic( GameStageConfig.class )
			.define( "crd_penalty", Reader.map( Reader.number() ), ()->PENALTY.get(), v->PENALTY = GameStageValue.of( v ) );
	}

	private static void increase( OnClampedRegionalDifficultyGet data ) {
		data.crd += PENALTY.get( GameStageHelper.getGlobalGameStage() );
	}
}
