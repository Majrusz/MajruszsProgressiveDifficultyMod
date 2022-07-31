package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.config.GameStageDoubleConfig;
import com.majruszsdifficulty.gamemodifiers.DifficultyModifier;
import com.mlib.Random;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnPickupXpContext;
import com.mlib.gamemodifiers.data.OnPickupXpData;

public class ExperienceBonus extends DifficultyModifier {
	final GameStageDoubleConfig bonusMultiplier = new GameStageDoubleConfig( "BonusMultiplier", "Extra bonus multiplier to experience gathered from any source.", 0.0, 0.2, 0.4, 0.0, 10.0 );

	public ExperienceBonus() {
		super( DifficultyModifier.DEFAULT, "ExperienceBonus", "Gives extra experience as the difficulty increases." );

		OnPickupXpContext onPickup = new OnPickupXpContext( this::giveExtraExperience );
		onPickup.addCondition( new Condition.Excludable() ).addConfig( this.bonusMultiplier );

		this.addContext( onPickup );
	}

	private void giveExtraExperience( OnPickupXpData data ) {
		double experience = data.event.getOrb().getValue();
		data.player.giveExperiencePoints( Random.roundRandomly( this.bonusMultiplier.getCurrentGameStageValue() * experience ) );
	}
}
