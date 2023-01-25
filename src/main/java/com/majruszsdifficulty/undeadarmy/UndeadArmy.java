package com.majruszsdifficulty.undeadarmy;

import com.mlib.Utility;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class UndeadArmy {
	final ServerLevel level;
	final Config config;
	final Data data;
	final ProgressIndicator progressIndicator;

	public UndeadArmy( ServerLevel level, Config config, Data data ) {
		this.level = level;
		this.config = config;
		this.data = data;
		this.progressIndicator = new ProgressIndicator( data );
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
		List< ServerPlayer > participants = this.getParticipants();
		switch( this.data.phase ) {
			case CREATED -> this.tickCreated();
			case WAVE_PREPARING -> this.tickWavePreparing();
			case WAVE_ONGOING -> this.tickWaveOngoing();
			case UNDEAD_DEFEATED -> this.tickUndeadDefeated();
			case UNDEAD_WON -> this.tickUndeadWon();
		}
		this.progressIndicator.tick( participants );
		this.data.phaseTicksLeft = Math.max( this.data.phaseTicksLeft - 1, 0 );
	}

	boolean hasFinished() {
		return this.data.phase == Phase.FINISHED;
	}

	double distanceTo( BlockPos position ) {
		int x = position.getX() - this.data.positionToAttack.getX();
		int z = position.getZ() - this.data.positionToAttack.getZ();

		return Math.sqrt( x * x + z * z );
	}

	boolean isInRange( BlockPos position ) {
		return this.distanceTo( position ) < 100.0f;
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
		if( this.isPhaseNotOver() )
			return;

		this.finish();
	}

	private List< ServerPlayer > getParticipants() {
		return this.level.getPlayers( player->player.isAlive() && this.isInRange( player.blockPosition() ) );
	}

	private boolean isLastWave() {
		return this.data.currentWave == 3;
	}

	private boolean isPhaseNotOver() {
		return this.data.phaseTicksLeft > 0;
	}
}
