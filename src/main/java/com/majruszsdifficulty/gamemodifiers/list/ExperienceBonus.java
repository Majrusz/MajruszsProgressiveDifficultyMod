package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.config.GameStageDoubleConfig;
import com.mlib.Random;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnPickupXp;
import com.mlib.math.Range;

@AutoInstance
public class ExperienceBonus {
	final GameStageDoubleConfig bonusMultiplier = new GameStageDoubleConfig( 0.0, 0.2, 0.4, new Range<>( 0.0, 10.0 ) );

	public ExperienceBonus() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "ExperienceBonus" )
			.comment( "Gives extra experience as the difficulty increases." );

		OnPickupXp.listen( this::giveExtraExperience )
			.addCondition( Condition.excludable() )
			.addConfig( this.bonusMultiplier.name( "BonusMultiplier" ).comment( "Extra bonus multiplier to experience gathered from any source." ) )
			.insertTo( group );
	}

	private void giveExtraExperience( OnPickupXp.Data data ) {
		double experience = data.event.getOrb().getValue();
		data.player.giveExperiencePoints( Random.roundRandomly( this.bonusMultiplier.getCurrentGameStageValue() * experience ) );
	}
}
