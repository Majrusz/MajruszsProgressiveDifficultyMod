package com.majruszsdifficulty.undeadarmy;

import com.mlib.Utility;

class WaveController implements IComponent {
	final UndeadArmy undeadArmy;

	public WaveController( UndeadArmy undeadArmy ) {
		this.undeadArmy = undeadArmy;
	}

	@Override
	public void tick() {
		switch( this.undeadArmy.phase.state ) {
			case CREATED -> this.tickCreated();
			case WAVE_PREPARING -> this.tickWavePreparing();
			case WAVE_ONGOING -> this.tickWaveOngoing();
			case UNDEAD_DEFEATED -> this.tickUndeadDefeated();
			case UNDEAD_WON -> this.tickUndeadWon();
		}
		this.undeadArmy.phase.ticksLeft = Math.max( this.undeadArmy.phase.ticksLeft - 1, 0 );
	}

	@Override
	public void onPhaseChanged() {

	}

	private void tickCreated() {
		if( !this.undeadArmy.isPhaseOver() )
			return;

		this.undeadArmy.setState( Phase.State.WAVE_PREPARING, Utility.secondsToTicks( 2.0 ) );
		// this.undeadArmy.generateWaveMobs( this.config, this.level );
	}

	private void tickWavePreparing() {
		if( !this.undeadArmy.isPhaseOver() )
			return;

		++this.undeadArmy.currentWave;
		this.undeadArmy.setState( Phase.State.WAVE_ONGOING, Utility.secondsToTicks( 5.0 ) );
	}

	private void tickWaveOngoing() {
		this.undeadArmy.mobsLeft.removeIf( mobInfo->mobInfo.uuid != null && mobInfo.toEntity( this.undeadArmy.level ) == null );

		if( this.undeadArmy.mobsLeft.isEmpty() ) {
			// this.data.setPhase( Phase.State.UNDEAD_WON, Utility.secondsToTicks( 2.0 ) );
			if( this.undeadArmy.isLastWave() ) {
				this.undeadArmy.setState( Phase.State.UNDEAD_DEFEATED, Utility.secondsToTicks( 2.0 ) );
			} else {
				this.undeadArmy.setState( Phase.State.WAVE_PREPARING, Utility.secondsToTicks( 2.0 ) );
				// this.undeadArmy.generateWaveMobs( this.config, this.level );
			}
		}
	}

	private void tickUndeadDefeated() {
		if( !this.undeadArmy.isPhaseOver() )
			return;

		this.undeadArmy.finish();
	}

	private void tickUndeadWon() {
		if( !this.undeadArmy.isPhaseOver() )
			return;

		this.undeadArmy.finish();
	}
}
