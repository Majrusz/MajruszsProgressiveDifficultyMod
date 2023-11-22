package com.majruszsdifficulty.bloodmoon;

import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszsdifficulty.data.WorldData;

public class BloodMoonHelper {
	public static void start() {
		if( WorldData.getBloodMoon().start() ) {
			WorldData.setDirty();
		}
	}

	public static void stop() {
		if( WorldData.getBloodMoon().finish() ) {
			WorldData.setDirty();
		}
	}

	@OnlyIn( Dist.CLIENT )
	public static float getColorRatio() {
		return BloodMoonClient.COLOR_RATIO;
	}

	public static boolean isActive() {
		return WorldData.getBloodMoon().isActive();
	}
}
