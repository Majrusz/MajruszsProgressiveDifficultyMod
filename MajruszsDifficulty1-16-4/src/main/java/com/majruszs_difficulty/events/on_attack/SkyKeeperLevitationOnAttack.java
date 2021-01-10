package com.majruszs_difficulty.events.on_attack;

import com.majruszs_difficulty.ConfigHandler.Config;
import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.entities.SkyKeeperEntity;
import net.minecraft.potion.Effects;
import net.minecraft.world.Difficulty;

/** Making Sky Keeper attacks have a chance to set levitation on enemies. */
public class SkyKeeperLevitationOnAttack extends OnAttackStackableEffectBase {
	public SkyKeeperLevitationOnAttack() {
		super( SkyKeeperEntity.class, GameState.Mode.EXPERT, true, Effects.LEVITATION, false, true, 1, MajruszsHelper.secondsToTicks( 60.0 ) );
	}

	@Override
	protected boolean isEnabled() {
		return !Config.isDisabled( Config.Features.SKY_KEEPER_LEVITATION );
	}

	@Override
	protected double getChance() {
		return Config.getChance( Config.Chances.SKY_KEEPER_LEVITATION );
	}

	@Override
	protected int getDurationInTicks( Difficulty difficulty ) {
		switch( difficulty ) {
			default:
				return MajruszsHelper.secondsToTicks( 3.0 );
			case NORMAL:
				return MajruszsHelper.secondsToTicks( 4.0 );
			case HARD:
				return MajruszsHelper.secondsToTicks( 5.0 );
		}
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return 0;
	}
}
