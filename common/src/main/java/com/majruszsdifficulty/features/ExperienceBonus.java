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
	private static final GameStageValue< Float > BONUS = GameStageValue.of(
		DefaultMap.defaultEntry( 0.0f ),
		DefaultMap.entry( GameStage.EXPERT_ID, 0.2f ),
		DefaultMap.entry( GameStage.MASTER_ID, 0.4f )
	);

	static {
		OnExpOrbPickedUp.listen( ExperienceBonus::increase );

		Serializables.getStatic( Config.Features.class )
			.define( "experience_bonus", ExperienceBonus.class );

		Serializables.getStatic( ExperienceBonus.class )
			.define( "chance", Reader.map( Reader.number() ), ()->BONUS.get(), v->BONUS.set( Range.of( 0.0f, 10.0f ).clamp( v ) ) );
	}

	private static void increase( OnExpOrbPickedUp data ) {
		data.experience += Random.round( data.original * BONUS.get( GameStageHelper.determineGameStage( data ) ) );
	}
}
