package com.majruszsdifficulty.undeadarmy;

import com.mlib.Utility;
import com.mlib.math.VectorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class UndeadArmy {
	final ServerLevel level;
	final Config config;
	final UndeadArmyData data;
	final ProgressIndicator progressIndicator;

	public UndeadArmy( ServerLevel level, Config config, UndeadArmyData data ) {
		this.level = level;
		this.config = config;
		this.data = data;
		this.progressIndicator = new ProgressIndicator( config, data );
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

	void tick() {
		List< ServerPlayer > participants = this.getParticipants();
		switch( this.data.getPhase() ) {
			case CREATED -> this.tickCreated();
			case WAVE_PREPARING -> this.tickWavePreparing();
			case WAVE_ONGOING -> this.tickWaveOngoing();
			case UNDEAD_DEFEATED -> this.tickUndeadDefeated();
			case UNDEAD_WON -> this.tickUndeadWon();
		}
		this.progressIndicator.tick( participants );
		this.data.decreaseTicksLeft();
	}

	boolean hasFinished() {
		return this.data.getPhase() == Phase.FINISHED;
	}

	double distanceTo( BlockPos position ) {
		return VectorHelper.distanceHorizontal( position.getCenter(), this.data.getPosition().getCenter() );
	}

	boolean isInRange( BlockPos position ) {
		return this.distanceTo( position ) < 100.0f;
	}

	UndeadArmyData getData() {
		return this.data;
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
		this.data.increaseWave();
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
		return this.data.getCurrentWave() == this.config.getWavesNum();
	}

	private boolean isPhaseNotOver() {
		return this.data.getPhaseRatio() < 1.0f;
	}
}
