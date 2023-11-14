package com.majruszsdifficulty.data;

import com.mlib.data.Serializables;
import com.mlib.math.Range;
import net.minecraft.world.effect.MobEffect;

import java.util.function.Supplier;

public class EffectDef {
	public Supplier< ? extends MobEffect > effect;
	public int amplifier;
	public float duration;

	static {
		Serializables.get( EffectDef.class )
			.defineEffect( "effect", s->s.effect.get(), ( s, v )->s.effect = ()->v )
			.defineInteger( "amplifier", s->s.amplifier, ( s, v )->s.amplifier = Range.of( 0, 10 ).clamp( v ) )
			.defineFloat( "duration", s->s.duration, ( s, v )->s.duration = Range.of( 1.0f, 1000.0f ).clamp( v ) );
	}

	public EffectDef( Supplier< ? extends MobEffect > effect, int amplifier, float duration ) {
		this.effect = effect;
		this.amplifier = amplifier;
		this.duration = duration;
	}

	public EffectDef( int amplifier, float duration ) {
		this( ()->null, amplifier, duration );
	}

	public EffectDef() {
		this( ()->null, 0, 1.0f );
	}
}
