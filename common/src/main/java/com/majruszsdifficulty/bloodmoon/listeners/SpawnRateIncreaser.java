package com.majruszsdifficulty.bloodmoon.listeners;

import com.majruszlibrary.events.OnMobSpawnLimitGet;
import com.majruszlibrary.events.OnMobSpawnRateGet;
import com.majruszsdifficulty.bloodmoon.BloodMoonConfig;
import com.majruszsdifficulty.bloodmoon.BloodMoonHelper;

public class SpawnRateIncreaser {
	static {
		OnMobSpawnRateGet.listen( SpawnRateIncreaser::increase )
			.addCondition( data->BloodMoonHelper.isActive() );

		OnMobSpawnLimitGet.listen( SpawnRateIncreaser::increase )
			.addCondition( data->BloodMoonHelper.isActive() );
	}

	private static void increase( OnMobSpawnRateGet data ) {
		data.value *= BloodMoonConfig.SPAWN_RATE_MULTIPLIER;
	}

	private static void increase( OnMobSpawnLimitGet data ) {
		data.value *= BloodMoonConfig.SPAWN_RATE_MULTIPLIER;
	}
}
