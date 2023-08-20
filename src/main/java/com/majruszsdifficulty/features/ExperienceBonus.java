package com.majruszsdifficulty.features;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.config.GameStageConfig;
import com.mlib.Random;
import com.mlib.config.ConfigGroup;
import com.mlib.contexts.OnPickupXp;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.math.Range;
import com.mlib.modhelper.AutoInstance;

@AutoInstance
public class ExperienceBonus {
	final GameStageConfig< Double > bonusMultiplier = GameStageConfig.create( 0.0, 0.2, 0.4, new Range<>( 0.0, 10.0 ) );

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
		data.player.giveExperiencePoints( Random.round( this.bonusMultiplier.getCurrentGameStageValue() * experience ) );
	}
}
