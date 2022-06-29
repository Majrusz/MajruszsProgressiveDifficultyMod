package com.majruszsdifficulty.gamemodifiers;

import com.majruszsdifficulty.MajruszsDifficulty;

public abstract class GameModifier extends com.mlib.gamemodifiers.GameModifier {
	public static final String DEFAULT = MajruszsDifficulty.MOD_ID + "Default";
	public static final String UNDEAD_ARMY = MajruszsDifficulty.MOD_ID + "UndeadArmy";
	public static final String GAME_STAGE = MajruszsDifficulty.MOD_ID + "GameStage";
	public static final String TREASURE_BAG = MajruszsDifficulty.MOD_ID + "TreasureBag";
	public static final String ACCESSORY = MajruszsDifficulty.MOD_ID + "Accessory";

	public GameModifier( String configKey, String configName, String configComment ) {
		super( configKey, configName, configComment );
	}

	public GameModifier( String configKey ) {
		super( configKey );
	}
}
