package com.majruszsdifficulty.undeadarmy;

import com.mlib.Utility;
import com.mlib.data.Data;
import com.mlib.data.SerializableStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

class UndeadArmyData extends SerializableStructure {
	private final Data< BlockPos > positionToAttack = this.addBlockPos().key( Keys.POSITION ).or( BlockPos.ZERO );
	private final Data< Direction > direction = this.addEnum( Direction::values ).key( Keys.DIRECTION ).or( Direction.EAST );
	private final Data< Phase > phase = this.addEnum( Phase::values ).key( Keys.PHASE ).or( Phase.CREATED );
	private final Data< Integer > phaseTicksLeft = this.addInteger().key( Keys.PHASE_TICKS_LEFT ).or( Utility.secondsToTicks( 2.0 ) );
	private final Data< Integer > phaseTicksTotal = this.addInteger().key( Keys.PHASE_TICKS_TOTAL ).or( Utility.secondsToTicks( 2.0 ) );
	private final Data< Integer > currentWave = this.addInteger().key( Keys.CURRENT_WAVE ).or( 0 );

	public UndeadArmyData( BlockPos positionToAttack, Direction direction ) {
		this.positionToAttack.set( positionToAttack );
		this.direction.set( direction );
	}

	public UndeadArmyData( CompoundTag tag ) {
		this.read( tag );
	}

	public BlockPos getPosition() {
		return this.positionToAttack.get();
	}

	public Direction getDirection() {
		return this.direction.get();
	}

	public Phase getPhase() {
		return this.phase.get();
	}

	public void setPhase( Phase phase, int ticksLeft ) {
		this.phase.set( phase );
		this.phaseTicksLeft.set( ticksLeft );
		this.phaseTicksTotal.set( Math.max( ticksLeft, 1 ) );
	}

	public void setPhase( Phase phase ) {
		this.setPhase( phase, 0 );
	}

	public void decreaseTicksLeft() {
		this.phaseTicksLeft.set( ticksLeft->Math.max( ticksLeft - 1, 0 ) );
	}

	public float getPhaseRatio() {
		return Mth.clamp( 1.0f - ( float )this.phaseTicksLeft.get() / this.phaseTicksTotal.get(), 0.0f, 1.0f );
	}

	public void increaseWave() {
		this.currentWave.set( wave->wave + 1 );
	}

	public int getCurrentWave() {
		return this.currentWave.get();
	}
}
