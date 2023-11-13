package com.majruszsdifficulty.data;

import com.mlib.data.Serializables;
import com.mlib.math.Range;

public class EffectDef {
	public int amplifier;
	public float duration;

	static {
		Serializables.get( EffectDef.class )
			.defineInteger( "amplifier", s->s.amplifier, ( s, v )->s.amplifier = Range.of( 0, 10 ).clamp( v ) )
			.defineFloat( "duration", s->s.duration, ( s, v )->s.duration = Range.of( 1.0f, 1000.0f ).clamp( v ) );
	}

	public EffectDef( int amplifier, float duration ) {
		this.amplifier = amplifier;
		this.duration = duration;
	}

	public EffectDef() {
		this( 0, 1.0f );
	}
}
