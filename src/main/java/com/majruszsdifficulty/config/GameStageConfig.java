package com.majruszsdifficulty.config;

import com.majruszsdifficulty.gamestage.GameStage;
import com.mlib.config.ConfigGroup;
import com.mlib.config.ValueConfig;

import java.util.function.Supplier;

public class GameStageConfig< Type > extends ConfigGroup implements Supplier< Type > {
	final ValueConfig< Type > normal, expert, master;

	public GameStageConfig( ValueConfig< Type > normal, ValueConfig< Type > expert, ValueConfig< Type > master ) {
		this.normal = normal;
		this.expert = expert;
		this.master = master;

		this.addConfigs( this.normal, this.expert, this.master );
	}

	public Type getCurrentGameStageValue() {
		return GameStage.getCurrentGameStageDependentValue( this.normal.get(), this.expert.get(), this.master.get() );
	}

	@Override
	public Type get() {
		return this.getCurrentGameStageValue();
	}
}
