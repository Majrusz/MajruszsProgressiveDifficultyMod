package com.majruszsdifficulty.gamemodifiers;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.mlib.gamemodifiers.Context;

public abstract class GameModifier extends com.mlib.gamemodifiers.GameModifier {
	public static final String DEFAULT = MajruszsDifficulty.MOD_ID + "Default";
	public static final String UNDEAD_ARMY = MajruszsDifficulty.MOD_ID + "UndeadArmy";
	public static final String GAME_STAGE = MajruszsDifficulty.MOD_ID + "GameStage";
	public static final String TREASURE_BAG = MajruszsDifficulty.MOD_ID + "TreasureBag";

	public GameModifier( String configKey, String configName, String configComment, Context... contexts ) {
		super( configKey, configName, configComment, contexts );
	}

	public GameModifier( String configKey, Context... contexts ) {
		super( configKey, contexts );
	}
}
