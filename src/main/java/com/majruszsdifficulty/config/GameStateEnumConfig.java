package com.majruszsdifficulty.config;

import com.majruszsdifficulty.GameStage;
import com.mlib.config.BaseConfig;
import net.minecraftforge.common.ForgeConfigSpec;

/** Basic config that stores game stage enum. */
public class GameStageEnumConfig extends BaseConfig< GameStage.Stage > {
	public ForgeConfigSpec.EnumValue< GameStage.Stage > enumValue;
	protected final GameStage.Stage defaultValue;

	public GameStageEnumConfig( String name, String comment, boolean requiresWorldRestart, GameStage.Stage defaultValue ) {
		super( name, comment, requiresWorldRestart );
		this.defaultValue = defaultValue;
	}

	public GameStage.Stage get() {
		return this.enumValue.get();
	}

	public void build( ForgeConfigSpec.Builder builder ) {
		super.build( builder );
		this.enumValue = builder.defineEnum( this.name, this.defaultValue );
	}
}
