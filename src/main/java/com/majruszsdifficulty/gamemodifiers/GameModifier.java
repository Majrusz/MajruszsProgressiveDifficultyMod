package com.majruszsdifficulty.gamemodifiers;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.mlib.gamemodifiers.Context;

public abstract class GameModifier extends com.mlib.gamemodifiers.GameModifier {
	public static final String DEFAULT = MajruszsDifficulty.MOD_ID + "Default";
	public static final String UNDEAD_ARMY = MajruszsDifficulty.MOD_ID + "UndeadArmy";

	public GameModifier( String configKey, String configName, String configComment, Context... contexts ) {
		super( configKey, configName, configComment, contexts );
	}
}
