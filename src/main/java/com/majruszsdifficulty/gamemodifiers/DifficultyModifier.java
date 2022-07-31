package com.majruszsdifficulty.gamemodifiers;

import com.majruszsdifficulty.Registries;
import com.mlib.gamemodifiers.GameModifier;

public abstract class DifficultyModifier extends GameModifier {
	public static final String DEFAULT = Registries.getLocationString( "default" );
	public static final String UNDEAD_ARMY = Registries.getLocationString( "undead_army" );
	public static final String GAME_STAGE = Registries.getLocationString( "game_stage" );
	public static final String TREASURE_BAG = Registries.getLocationString( "treasure_bag" );
	public static final String ACCESSORY = Registries.getLocationString( "accessory" );

	public DifficultyModifier( String configKey, String configName, String configComment ) {
		super( configKey, configName, configComment );
	}
}
