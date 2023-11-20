package com.majruszsdifficulty.data;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.math.Range;
import net.minecraft.world.effect.MobEffect;

import java.util.function.Supplier;

public class EffectDef {
	public Supplier< ? extends MobEffect > effect;
	public int amplifier;
	public float duration;

	static {
		Serializables.get( EffectDef.class )
			.define( "effect", Reader.mobEffect(), s->s.effect.get(), ( s, v )->s.effect = ()->v )
			.define( "amplifier", Reader.integer(), s->s.amplifier, ( s, v )->s.amplifier = Range.of( 0, 10 ).clamp( v ) )
			.define( "duration", Reader.number(), s->s.duration, ( s, v )->s.duration = Range.of( 1.0f, 1000.0f ).clamp( v ) );
	}

	public EffectDef( Supplier< ? extends MobEffect > effect, int amplifier, float duration ) {
		this.effect = effect;
		this.amplifier = amplifier;
		this.duration = duration;
	}

	public EffectDef() {
		this( ()->null, 0, 1.0f );
	}
}
