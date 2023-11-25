package com.majruszsdifficulty.features;

import com.majruszlibrary.collection.DefaultMap;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.events.OnExpOrbPickedUp;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;

public class ExperienceBonus {
	private static boolean IS_ENABLED = true;
	private static GameStageValue< Float > BONUS = GameStageValue.of(
		DefaultMap.defaultEntry( 0.0f ),
		DefaultMap.entry( GameStage.EXPERT_ID, 0.2f ),
		DefaultMap.entry( GameStage.MASTER_ID, 0.4f )
	);

	static {
		OnExpOrbPickedUp.listen( ExperienceBonus::increase )
			.addCondition( data->IS_ENABLED );

		Serializables.getStatic( Config.Features.class )
			.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v )
			.define( "experience_bonus", Reader.map( Reader.number() ), ()->BONUS.get(), v->BONUS = GameStageValue.of( Range.of( 0.0f, 10.0f ).clamp( v ) ) );
	}

	private static void increase( OnExpOrbPickedUp data ) {
		data.experience += Random.round( data.original * BONUS.get( GameStageHelper.determineGameStage( data ) ) );
	}
}
