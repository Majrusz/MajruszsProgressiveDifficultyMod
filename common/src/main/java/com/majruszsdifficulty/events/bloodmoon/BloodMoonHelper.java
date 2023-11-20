package com.majruszsdifficulty.events.bloodmoon;

import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszsdifficulty.MajruszsDifficulty;

public class BloodMoonHelper {
	public static void start() {
		if( MajruszsDifficulty.WORLD_DATA.getBloodMoon().start() ) {
			MajruszsDifficulty.WORLD_DATA.setDirty();
		}
	}

	public static void stop() {
		if( MajruszsDifficulty.WORLD_DATA.getBloodMoon().finish() ) {
			MajruszsDifficulty.WORLD_DATA.setDirty();
		}
	}

	@OnlyIn( Dist.CLIENT )
	public static float getColorRatio() {
		return BloodMoonClient.COLOR_RATIO;
	}

	public static boolean isActive() {
		return MajruszsDifficulty.WORLD_DATA.getBloodMoon().isActive();
	}
}
