package com.majruszsdifficulty.undeadarmy;

import com.mlib.Utility;

class WaveController implements IComponent {
	final UndeadArmy undeadArmy;

	public WaveController( UndeadArmy undeadArmy ) {
		this.undeadArmy = undeadArmy;
	}

	@Override
	public void tick() {
		switch( this.undeadArmy.phase ) {
			case CREATED -> this.tickCreated();
			case WAVE_PREPARING -> this.tickWavePreparing();
			case WAVE_ONGOING -> this.tickWaveOngoing();
			case UNDEAD_DEFEATED -> this.tickUndeadDefeated();
			case UNDEAD_WON -> this.tickUndeadWon();
		}
		this.undeadArmy.phaseTicksLeft = Math.max( this.undeadArmy.phaseTicksLeft - 1, 0 );
	}

	@Override
	public void onPhaseChanged() {

	}

	private void tickCreated() {
		if( !this.undeadArmy.isPhaseOver() )
			return;

		this.undeadArmy.setPhase( Phase.WAVE_PREPARING, Utility.secondsToTicks( 2.0 ) );
		// this.undeadArmy.generateWaveMobs( this.config, this.level );
	}

	private void tickWavePreparing() {
		if( !this.undeadArmy.isPhaseOver() )
			return;

		++this.undeadArmy.currentWave;
		this.undeadArmy.setPhase( Phase.WAVE_ONGOING, Utility.secondsToTicks( 5.0 ) );
	}

	private void tickWaveOngoing() {
		this.undeadArmy.pendingMobs.removeIf( mobInfo->mobInfo.id != null && mobInfo.toEntity( this.undeadArmy.level ) == null );

		if( this.undeadArmy.pendingMobs.isEmpty() ) {
			// this.data.setPhase( Phase.UNDEAD_WON, Utility.secondsToTicks( 2.0 ) );
			if( this.undeadArmy.isLastWave() ) {
				this.undeadArmy.setPhase( Phase.UNDEAD_DEFEATED, Utility.secondsToTicks( 2.0 ) );
			} else {
				this.undeadArmy.setPhase( Phase.WAVE_PREPARING, Utility.secondsToTicks( 2.0 ) );
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
