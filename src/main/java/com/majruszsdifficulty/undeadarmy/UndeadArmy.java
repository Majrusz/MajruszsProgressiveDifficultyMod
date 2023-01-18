package com.majruszsdifficulty.undeadarmy;

import com.mlib.MajruszLibrary;
import com.mlib.Utility;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

public class UndeadArmy {
	final ProgressIndicator progressIndicator = new ProgressIndicator();
	final ServerLevel level;
	final Data data;

	public UndeadArmy( ServerLevel level, BlockPos positionToAttack, Direction direction ) {
		this.level = level;
		this.data = new Data( positionToAttack, direction );
	}

	public UndeadArmy( ServerLevel level, CompoundTag nbt ) {
		this.level = level;
		this.data = new Data( nbt );
	}

	public void highlightArmy() {

	}

	public void killAllUndeadArmyMobs() {

	}

	public int countMobsLeft() {
		return 0;
	}

	public void finish() {
		this.data.setPhase( Phase.FINISHED );
	}

	CompoundTag write( CompoundTag nbt ) {
		return this.data.write( nbt );
	}

	void tick() {
		MajruszLibrary.log( "Status: %s, Wave: %s, TicksLeft: %s", this.data.phase, this.data.currentWave, this.data.phaseTicksLeft );
		switch( this.data.phase ) {
			case CREATED -> this.tickCreated();
			case WAVE_PREPARING -> this.tickWavePreparing();
			case WAVE_ONGOING -> this.tickWaveOngoing();
			case UNDEAD_DEFEATED -> this.tickUndeadDefeated();
			case UNDEAD_WON -> this.tickUndeadWon();
		}
		this.data.phaseTicksLeft = Math.max( this.data.phaseTicksLeft - 1, 0 );
	}

	boolean hasFinished() {
		return this.data.phase == Phase.FINISHED;
	}

	double distanceTo( BlockPos position ) {
		return position.distSqr( this.data.positionToAttack );
	}

	private void tickCreated() {
		if( this.isPhaseNotOver() )
			return;

		this.data.setPhase( Phase.WAVE_PREPARING, Utility.secondsToTicks( 2.0 ) );
	}

	private void tickWavePreparing() {
		if( this.isPhaseNotOver() )
			return;

		this.data.setPhase( Phase.WAVE_ONGOING, Utility.secondsToTicks( 2.0 ) );
		++this.data.currentWave;
	}

	private void tickWaveOngoing() {
		if( this.isPhaseNotOver() )
			return;

		if( this.isLastWave() ) {
			this.data.setPhase( Phase.UNDEAD_DEFEATED, Utility.secondsToTicks( 2.0 ) );
		} else {
			this.data.setPhase( Phase.WAVE_PREPARING, Utility.secondsToTicks( 2.0 ) );
		}
	}

	private void tickUndeadDefeated() {
		if( this.isPhaseNotOver() )
			return;

		this.finish();
	}

	private void tickUndeadWon() {

	}

	private boolean isLastWave() {
		return this.data.currentWave == 3;
	}

	private boolean isPhaseNotOver() {
		return this.data.phaseTicksLeft > 0;
	}
}
