package com.majruszsdifficulty.gamemodifiers;

import com.mlib.gamemodifiers.Context;

public abstract class GameModifier extends com.mlib.gamemodifiers.GameModifier {
	public static final String DEFAULT = "Default";
	public static final String UNDEAD_ARMY = "UndeadArmy";

	public GameModifier( String configKey, String configName, String configComment, Context... contexts ) {
		super( configKey, configName, configComment, contexts );
	}
}
