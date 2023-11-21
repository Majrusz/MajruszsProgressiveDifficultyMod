package com.majruszsdifficulty.bloodmoon.listeners;

import com.majruszlibrary.events.OnMobSpawnLimitGet;
import com.majruszlibrary.events.OnMobSpawnRateGet;
import com.majruszsdifficulty.bloodmoon.BloodMoonHelper;

public class SpawnRateIncreaser {
	static {
		OnMobSpawnRateGet.listen( SpawnRateIncreaser::increase );

		OnMobSpawnLimitGet.listen( SpawnRateIncreaser::increase );
	}

	private static void increase( OnMobSpawnRateGet data ) {
		data.value *= BloodMoonHelper.isActive() ? 2.0f : 1.0f;
	}

	private static void increase( OnMobSpawnLimitGet data ) {
		data.value *= BloodMoonHelper.isActive() ? 2.0f : 1.0f;
	}
}
