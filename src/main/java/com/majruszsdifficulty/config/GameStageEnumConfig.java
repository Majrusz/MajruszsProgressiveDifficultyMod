package com.majruszsdifficulty.config;

import com.majruszsdifficulty.GameStage;
import com.mlib.config.ValueConfig;
import net.minecraftforge.common.ForgeConfigSpec;

public class GameStageEnumConfig extends ValueConfig< GameStage.Stage, ForgeConfigSpec.EnumValue< GameStage.Stage > > {
	public GameStageEnumConfig( String name, String comment, boolean requiresWorldRestart, GameStage.Stage defaultValue ) {
		super( name, comment, requiresWorldRestart, defaultValue );
	}

	@Override
	public ForgeConfigSpec.EnumValue< GameStage.Stage > buildValue( ForgeConfigSpec.Builder builder ) {
		return builder.defineEnum( this.name, this.defaultValue );
	}
}
