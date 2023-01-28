package com.majruszsdifficulty.undeadarmy;

import com.mlib.Utility;
import com.mlib.data.SerializableStructure;
import net.minecraft.util.Mth;

class Phase extends SerializableStructure {
	State state = State.CREATED;
	int ticksLeft = Utility.secondsToTicks( 2.0 );
	int ticksTotal = Utility.secondsToTicks( 2.0 );
	int healthTotal = 0;

	public Phase() {
		this.define( "state", ()->this.state, x->this.state = x, State::values );
		this.define( "ticks_left", ()->this.ticksLeft, x->this.ticksLeft = x );
		this.define( "ticks_total", ()->this.ticksTotal, x->this.ticksTotal = x );
		this.define( "health_total", ()->this.healthTotal, x->this.healthTotal = x );
	}

	float getRatioLeft() {
		return Mth.clamp( 1.0f - ( float )this.ticksLeft / this.ticksTotal, 0.0f, 1.0f );
	}

	enum State {
		CREATED, WAVE_PREPARING, WAVE_ONGOING, UNDEAD_DEFEATED, UNDEAD_WON, FINISHED
	}
}
