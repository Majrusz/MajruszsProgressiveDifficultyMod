package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.config.GameStageDoubleConfig;
import com.mlib.gamemodifiers.GameModifier;import com.majruszsdifficulty.Registries;
import com.mlib.Random;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnPickupXp;

public class ExperienceBonus extends GameModifier {
	final GameStageDoubleConfig bonusMultiplier = new GameStageDoubleConfig( "BonusMultiplier", "Extra bonus multiplier to experience gathered from any source.", 0.0, 0.2, 0.4, 0.0, 10.0 );

	public ExperienceBonus() {
		super( Registries.Modifiers.DEFAULT, "ExperienceBonus", "Gives extra experience as the difficulty increases." );

		OnPickupXp.Context onPickup = new OnPickupXp.Context( this::giveExtraExperience );
		onPickup.addCondition( new Condition.Excludable<>() ).addConfig( this.bonusMultiplier );

		this.addContext( onPickup );
	}

	private void giveExtraExperience( OnPickupXp.Data data ) {
		double experience = data.event.getOrb().getValue();
		data.player.giveExperiencePoints( Random.roundRandomly( this.bonusMultiplier.getCurrentGameStageValue() * experience ) );
	}
}
