package com.majruszsdifficulty.bloodmoon;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszsdifficulty.data.Config;

public class BloodMoonConfig {
	public static boolean IS_ENABLED = true;

	static {
		Serializables.getStatic( Config.class )
			.define( "blood_moon", BloodMoonConfig.class );

		Serializables.getStatic( BloodMoonConfig.class )
			.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v );
	}
}
