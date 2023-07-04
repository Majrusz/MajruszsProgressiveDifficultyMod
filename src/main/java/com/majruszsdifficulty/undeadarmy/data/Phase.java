package com.majruszsdifficulty.undeadarmy.data;

import com.mlib.data.SerializableStructure;
import net.minecraft.util.Mth;

public class Phase extends SerializableStructure {
	public State state = State.CREATED;
	public int ticksLeft = 0;
	public int ticksTotal = 1;
	public int healthTotal = 0;

	public Phase() {
		this.defineEnum( "state", ()->this.state, x->this.state = x, State::values );
		this.defineInteger( "ticks_left", ()->this.ticksLeft, x->this.ticksLeft = x );
		this.defineInteger( "ticks_total", ()->this.ticksTotal, x->this.ticksTotal = x );
		this.defineInteger( "health_total", ()->this.healthTotal, x->this.healthTotal = x );
	}

	public float getRatio() {
		return Mth.clamp( 1.0f - ( float )this.ticksLeft / this.ticksTotal, 0.0f, 1.0f );
	}

	public float getTicksActive() {
		return this.ticksTotal - this.ticksLeft;
	}

	public enum State {
		CREATED, STARTED, WAVE_PREPARING, WAVE_ONGOING, UNDEAD_DEFEATED, UNDEAD_WON, FINISHED
	}
}
