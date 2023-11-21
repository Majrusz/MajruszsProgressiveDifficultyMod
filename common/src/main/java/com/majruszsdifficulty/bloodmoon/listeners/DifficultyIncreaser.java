package com.majruszsdifficulty.bloodmoon.listeners;

import com.majruszlibrary.events.OnClampedRegionalDifficultyGet;

public class DifficultyIncreaser {
	static {
		OnClampedRegionalDifficultyGet.listen( DifficultyIncreaser::increase );
	}

	private static void increase( OnClampedRegionalDifficultyGet data ) {
		data.crd += 0.5f;
	}
}
