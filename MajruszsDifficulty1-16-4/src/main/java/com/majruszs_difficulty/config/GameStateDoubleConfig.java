package com.majruszs_difficulty.config;

import com.majruszs_difficulty.GameState;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.config.IConfig;
import net.minecraftforge.common.ForgeConfigSpec;

public class GameStateDoubleConfig implements IConfig {
	protected final DoubleConfig normal;
	protected final DoubleConfig expert;
	protected final DoubleConfig master;

	public GameStateDoubleConfig( String name, String comment, double defaultValueNormal, double defaultValueExpert, double defaultValueMaster, double minimumValue, double maximumValue ) {
		this.normal = new DoubleConfig( name + "_normal", comment + " (Normal Mode)", false, defaultValueNormal, minimumValue, maximumValue );
		this.expert = new DoubleConfig( name + "_expert", comment + " (Expert Mode)", false, defaultValueExpert, minimumValue, maximumValue );
		this.master = new DoubleConfig( name + "_master", comment + " (Master Mode)", false, defaultValueMaster, minimumValue, maximumValue );
	}

	public double getCurrentGameStateValue() {
		switch( GameState.getCurrentMode() ) {
			default:
				return this.normal.get();
			case EXPERT:
				return this.expert.get();
			case MASTER:
				return this.master.get();
		}
	}

	@Override
	public void build( ForgeConfigSpec.Builder builder ) {
		this.normal.build( builder );
		this.expert.build( builder );
		this.master.build( builder );
	}
}
