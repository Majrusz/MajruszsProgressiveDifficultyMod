package com.majruszsdifficulty.bloodmoon;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.math.Range;

public class BloodMoonConfig {
	public static boolean IS_ENABLED = true;
	public static final Range< Long > TIME = Range.of( 12300L, 23600L );
	public static float NIGHT_TRIGGER_CHANCE = 0.0666f;

	static {
		Serializables.getStatic( BloodMoonConfig.class )
			.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v )
			.define( "night_trigger_chance", Reader.number(), ()->NIGHT_TRIGGER_CHANCE, v->NIGHT_TRIGGER_CHANCE = Range.CHANCE.clamp( v ) );
	}
}
