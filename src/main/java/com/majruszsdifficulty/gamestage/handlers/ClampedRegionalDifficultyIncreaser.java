package com.majruszsdifficulty.gamestage.handlers;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.config.GameStageConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.contexts.OnClampedRegionalDifficultyGet;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.math.Range;
import com.mlib.modhelper.AutoInstance;

@AutoInstance
public class ClampedRegionalDifficultyIncreaser {
	final GameStageConfig< Double > penalty = GameStageConfig.create( 0.0, 0.15, 0.3, new Range<>( 0.0, 1.0 ) );

	public ClampedRegionalDifficultyIncreaser() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.GAME_STAGE );

		OnClampedRegionalDifficultyGet.listen( this::increaseDifficulty )
			.addConfig( this.penalty.name( "ClampedRegionalDifficultyPenalty" )
				.comment( "Determines the value by which the Clamped Regional Difficulty is increased." ) )
			.insertTo( group );
	}

	private void increaseDifficulty( OnClampedRegionalDifficultyGet.Data data ) {
		data.value += this.penalty.getCurrentGameStageValue();
	}
}
