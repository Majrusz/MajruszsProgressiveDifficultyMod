package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.config.GameStageDoubleConfig;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.Random;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnPickupXpContext;

public class ExperienceBonus extends GameModifier {
	static final OnPickupXpContext ON_PICKUP = new OnPickupXpContext( ExperienceBonus::giveExtraExperience );
	static final GameStageDoubleConfig BONUS_MULTIPLIER = new GameStageDoubleConfig( "BonusMultiplier", "Extra bonus multiplier to experience gathered from any source.", 0.0, 0.2, 0.4, 0.0, 10.0 );

	static {
		ON_PICKUP.addCondition( new Condition.Excludable() );
		ON_PICKUP.addConfig( BONUS_MULTIPLIER );
	}

	public ExperienceBonus() {
		super( GameModifier.DEFAULT, "ExperienceBonus", "Gives extra experience as the difficulty increases.", ON_PICKUP );
	}

	private static void giveExtraExperience( com.mlib.gamemodifiers.GameModifier gameModifier, OnPickupXpContext.Data data ) {
		double experience = data.event.getOrb().getValue();
		data.player.giveExperiencePoints( Random.roundRandomly( BONUS_MULTIPLIER.getCurrentGameStageValue() * experience ) );
	}
}
