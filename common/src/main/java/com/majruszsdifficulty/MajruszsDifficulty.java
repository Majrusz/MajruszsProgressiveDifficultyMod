package com.majruszsdifficulty;

import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.data.WorldData;
import com.mlib.modhelper.ModHelper;

public class MajruszsDifficulty {
	public static final String MOD_ID = "majruszsdifficulty";
	public static final ModHelper HELPER = ModHelper.create( MOD_ID );

	// Data
	public static final Config CONFIG = HELPER.config( Config::new ).autoSync().create();
	public static final WorldData WORLD_DATA = HELPER.saved( WorldData::new );

	private MajruszsDifficulty() {}
}
