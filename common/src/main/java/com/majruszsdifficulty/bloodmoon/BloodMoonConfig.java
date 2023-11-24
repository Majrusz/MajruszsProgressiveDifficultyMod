package com.majruszsdifficulty.bloodmoon;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.math.Range;

public class BloodMoonConfig {
	public static boolean IS_ENABLED = true;
	public static final Range< Long > TIME = Range.of( 12300L, 23600L );

	static {
		Serializables.getStatic( BloodMoonConfig.class )
			.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v );
	}
}
