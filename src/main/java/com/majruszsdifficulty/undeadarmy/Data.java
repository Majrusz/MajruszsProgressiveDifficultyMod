package com.majruszsdifficulty.undeadarmy;

import com.mlib.Utility;
import com.mlib.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

class Data {
	public final BlockPos positionToAttack;
	public final Direction direction;
	public Phase phase;
	public int phaseTicksLeft;
	public int phaseTicksTotal;
	public int currentWave;

	public Data( BlockPos positionToAttack, Direction direction ) {
		this.positionToAttack = positionToAttack;
		this.direction = direction;
		this.phase = Phase.CREATED;
		this.phaseTicksLeft = Utility.secondsToTicks( 2.0 );
		this.phaseTicksTotal = this.phaseTicksLeft;
		this.currentWave = 0;
	}

	public Data( CompoundTag nbt ) {
		this.positionToAttack = NBTHelper.loadBlockPos( nbt, Keys.POSITION );
		this.direction = Direction.read( nbt );
		this.phase = Phase.read( nbt );
		this.phaseTicksLeft = nbt.getInt( Keys.PHASE_TICKS_LEFT );
		this.phaseTicksTotal = nbt.getInt( Keys.PHASE_TICKS_TOTAL );
		this.currentWave = nbt.getInt( Keys.CURRENT_WAVE );
	}

	public CompoundTag write( CompoundTag nbt ) {
		NBTHelper.saveBlockPos( nbt, Keys.POSITION, this.positionToAttack );
		this.direction.write( nbt );
		this.phase.write( nbt );
		nbt.putInt( Keys.PHASE_TICKS_LEFT, this.phaseTicksLeft );
		nbt.putInt( Keys.PHASE_TICKS_TOTAL, this.phaseTicksTotal );
		nbt.putInt( Keys.CURRENT_WAVE, this.currentWave );

		return nbt;
	}

	public void setPhase( Phase phase, int ticksLeft ) {
		this.phase = phase;
		this.phaseTicksLeft = ticksLeft;
		this.phaseTicksTotal = Math.max( ticksLeft, 1 );
	}

	public void setPhase( Phase phase ) {
		this.setPhase( phase, 0 );
	}

	public float getPhaseRatio() {
		return Mth.clamp( 1.0f - ( float )this.phaseTicksLeft / this.phaseTicksTotal, 0.0f, 1.0f );
	}
}
