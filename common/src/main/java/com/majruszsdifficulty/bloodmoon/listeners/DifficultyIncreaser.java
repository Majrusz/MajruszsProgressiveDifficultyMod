package com.majruszsdifficulty.bloodmoon.listeners;

import com.majruszlibrary.events.OnClampedRegionalDifficultyGet;
import com.majruszsdifficulty.bloodmoon.BloodMoonConfig;
import com.majruszsdifficulty.bloodmoon.BloodMoonHelper;

public class DifficultyIncreaser {
	static {
		OnClampedRegionalDifficultyGet.listen( DifficultyIncreaser::increase )
			.addCondition( data->BloodMoonHelper.isActive() );
	}

	private static void increase( OnClampedRegionalDifficultyGet data ) {
		data.crd += BloodMoonConfig.CRD_PENALTY;
	}
}
