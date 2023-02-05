package com.majruszsdifficulty.undeadarmy.components;

import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.majruszsdifficulty.undeadarmy.data.Phase;
import com.mlib.Utility;

record WaveController( UndeadArmy undeadArmy ) implements IComponent {
	@Override
	public void tick() {
		switch( this.undeadArmy.phase.state ) {
			case STARTED -> this.tickStarted();
			case WAVE_PREPARING -> this.tickWavePreparing();
			case WAVE_ONGOING -> this.tickWaveOngoing();
			case UNDEAD_DEFEATED -> this.tickUndeadDefeated();
			case UNDEAD_WON -> this.tickUndeadWon();
		}
		this.undeadArmy.phase.ticksLeft = Math.max( this.undeadArmy.phase.ticksLeft - 1, 0 );
	}

	private void tickStarted() {
		if( this.isPhaseOver() ) {
			this.undeadArmy.setState( Phase.State.WAVE_PREPARING, this.undeadArmy.config.getPreparationDuration() );
		}
	}

	private void tickWavePreparing() {
		if( this.isPhaseOver() ) {
			++this.undeadArmy.currentWave;
			this.undeadArmy.setState( Phase.State.WAVE_ONGOING, this.undeadArmy.config.getWaveDuration() );
		}
	}

	private void tickWaveOngoing() {
		this.undeadArmy.mobsLeft.removeIf( mobInfo->mobInfo.uuid != null && mobInfo.toEntity( this.undeadArmy.level ) == null );

		if( this.undeadArmy.mobsLeft.isEmpty() ) {
			if( this.undeadArmy.isLastWave() ) {
				this.undeadArmy.setState( Phase.State.UNDEAD_DEFEATED, Utility.secondsToTicks( 30.0 ) );
			} else {
				this.undeadArmy.setState( Phase.State.WAVE_PREPARING, this.undeadArmy.config.getPreparationDuration() );
			}
		} else if( this.isPhaseOver() ) {
			this.undeadArmy.setState( Phase.State.UNDEAD_WON, Utility.secondsToTicks( 30.0 ) );
		}
	}

	private void tickUndeadDefeated() {
		if( this.isPhaseOver() ) {
			this.undeadArmy.finish();
		}
	}

	private void tickUndeadWon() {
		if( this.isPhaseOver() ) {
			this.undeadArmy.finish();
		}
	}

	private boolean isPhaseOver() {
		return this.undeadArmy.phase.getRatio() == 1.0f;
	}
}
