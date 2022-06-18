package com.majruszsdifficulty.config;

import com.majruszsdifficulty.GameState;
import com.mlib.config.BaseConfig;
import net.minecraftforge.common.ForgeConfigSpec;

/** Basic config that stores game state enum. */
public class GameStateEnumConfig extends BaseConfig< GameState.State > {
	public ForgeConfigSpec.EnumValue< GameState.State > enumValue;
	protected final GameState.State defaultValue;

	public GameStateEnumConfig( String name, String comment, boolean requiresWorldRestart, GameState.State defaultValue ) {
		super( name, comment, requiresWorldRestart );
		this.defaultValue = defaultValue;
	}

	public GameState.State get() {
		return this.enumValue.get();
	}

	public void build( ForgeConfigSpec.Builder builder ) {
		super.build( builder );
		this.enumValue = builder.defineEnum( this.name, this.defaultValue );
	}
}
