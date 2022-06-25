package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.config.GameStageDoubleConfig;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.Random;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnPickupXpContext;

public class ExperienceBonus extends GameModifier {
	static final OnPickupXpContext ON_PICKUP = new OnPickupXpContext();

	static {
		ON_PICKUP.addCondition( new Condition.Excludable() );
	}

	final GameStageDoubleConfig bonusMultiplier;

	public ExperienceBonus() {
		super( GameModifier.DEFAULT, "ExperienceBonus", "Gives extra experience as the difficulty increases.", ON_PICKUP );
		this.bonusMultiplier = new GameStageDoubleConfig( "bonus_multiplier", "Extra bonus multiplier to experience gathered from any source.", 0.0, 0.2, 0.4, 0.0, 10.0 );
		this.configGroup.addConfig( this.bonusMultiplier );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnPickupXpContext.Data pickupData ) {
			double experience = pickupData.event.getOrb().getValue();
			pickupData.player.giveExperiencePoints( Random.roundRandomly( this.bonusMultiplier.getCurrentGameStageValue() * experience ) );
		}
	}
}
