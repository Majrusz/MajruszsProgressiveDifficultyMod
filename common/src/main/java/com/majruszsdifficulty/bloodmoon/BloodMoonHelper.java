package com.majruszsdifficulty.bloodmoon;

import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.platform.Side;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.data.WorldData;

public class BloodMoonHelper {
	private static BloodMoon BLOOD_MOON = new BloodMoon();

	static {
		Serializables.getStatic( WorldData.class )
			.define( "blood_moon", Reader.custom( BloodMoon::new ), ()->BLOOD_MOON, v->BLOOD_MOON = v );

		Serializables.getStatic( WorldData.Client.class )
			.define( "blood_moon", Reader.bool(), ()->BLOOD_MOON.isActive(), v->BLOOD_MOON.setActive( v ) );
	}

	public static boolean start() {
		if( BLOOD_MOON.start() ) {
			MajruszsDifficulty.WORLD_DATA.setDirty();
			return true;
		}

		return false;
	}

	public static boolean stop() {
		if( BLOOD_MOON.finish() ) {
			MajruszsDifficulty.WORLD_DATA.setDirty();
			return true;
		}

		return false;
	}

	@OnlyIn( Dist.CLIENT )
	public static float getColorRatio() {
		return BloodMoonClient.COLOR_RATIO;
	}

	public static boolean isActive() {
		return BLOOD_MOON.isActive();
	}

	public static boolean isValidDayTime() {
		return BloodMoonConfig.TIME.within( Side.getServer().overworld().getDayTime() );
	}
}
