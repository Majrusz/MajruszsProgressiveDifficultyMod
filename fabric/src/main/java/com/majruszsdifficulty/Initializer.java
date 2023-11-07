package com.majruszsdifficulty;

import net.fabricmc.api.ModInitializer;

public class Initializer implements ModInitializer {
	@Override
	public void onInitialize() {
		MajruszsDifficulty.HELPER.register();
	}
}
