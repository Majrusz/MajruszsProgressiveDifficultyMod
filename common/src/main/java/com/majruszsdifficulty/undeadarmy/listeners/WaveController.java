package com.majruszsdifficulty.undeadarmy.listeners;

import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.majruszsdifficulty.undeadarmy.UndeadArmyConfig;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyTicked;

public class WaveController {
	static {
		OnUndeadArmyTicked.listen( WaveController::update );
	}

	private static void update( OnUndeadArmyTicked data ) {
		switch( data.undeadArmy.phase.state ) {
			case STARTED -> WaveController.tickStarted( data.undeadArmy );
			case WAVE_PREPARING -> WaveController.tickWavePreparing( data.undeadArmy );
			case WAVE_ONGOING -> WaveController.tickWaveOngoing( data.undeadArmy );
			case UNDEAD_DEFEATED -> WaveController.tickUndeadDefeated( data.undeadArmy );
			case UNDEAD_WON -> WaveController.tickUndeadWon( data.undeadArmy );
		}
		data.undeadArmy.phase.ticksLeft = Math.max( data.undeadArmy.phase.ticksLeft - 1, 0 );
	}

	private static void tickStarted( UndeadArmy undeadArmy ) {
		if( WaveController.isPhaseOver( undeadArmy ) ) {
			undeadArmy.setState( UndeadArmy.Phase.State.WAVE_PREPARING, UndeadArmyConfig.PREPARING_DURATION );
		}
	}

	private static void tickWavePreparing( UndeadArmy undeadArmy ) {
		if( WaveController.isPhaseOver( undeadArmy ) ) {
			++undeadArmy.currentWave;
			undeadArmy.setState( UndeadArmy.Phase.State.WAVE_ONGOING, UndeadArmyConfig.WAVE_DURATION );
		}
	}

	private static void tickWaveOngoing( UndeadArmy undeadArmy ) {
		undeadArmy.mobsLeft.removeIf( mobInfo->mobInfo.uuid != null && mobInfo.toEntity( undeadArmy.getLevel() ) == null );

		if( undeadArmy.mobsLeft.isEmpty() ) {
			if( undeadArmy.isLastWave() ) {
				undeadArmy.setState( UndeadArmy.Phase.State.UNDEAD_DEFEATED, 30.0f );
			} else {
				undeadArmy.setState( UndeadArmy.Phase.State.WAVE_PREPARING, UndeadArmyConfig.PREPARING_DURATION );
			}
		} else if( WaveController.isPhaseOver( undeadArmy ) ) {
			undeadArmy.setState( UndeadArmy.Phase.State.UNDEAD_WON, 30.0f );
		}
	}

	private static void tickUndeadDefeated( UndeadArmy undeadArmy ) {
		if( WaveController.isPhaseOver( undeadArmy ) ) {
			undeadArmy.finish();
		}
	}

	private static void tickUndeadWon( UndeadArmy undeadArmy ) {
		if( WaveController.isPhaseOver( undeadArmy ) ) {
			undeadArmy.finish();
		}
	}

	private static boolean isPhaseOver( UndeadArmy undeadArmy ) {
		return undeadArmy.phase.getRatio() == 1.0f;
	}
}
