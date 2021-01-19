package com.majruszs_difficulty.config;

import com.majruszs_difficulty.GameState;
import com.mlib.config.DoubleConfig;
import com.mlib.config.IConfig;
import com.mlib.config.IntegerConfig;
import net.minecraftforge.common.ForgeConfigSpec;

public class GameStateIntegerConfig implements IConfig {
	protected final IntegerConfig normal;
	protected final IntegerConfig expert;
	protected final IntegerConfig master;

	public GameStateIntegerConfig( String name, String comment, int defaultValueNormal, int defaultValueExpert, int defaultValueMaster, int minimumValue, int maximumValue ) {
		this.normal = new IntegerConfig( name + "_normal", comment + " (Normal Mode)", false, defaultValueNormal, minimumValue, maximumValue );
		this.expert = new IntegerConfig( name + "_expert", comment + " (Expert Mode)", false, defaultValueExpert, minimumValue, maximumValue );
		this.master = new IntegerConfig( name + "_master", comment + " (Master Mode)", false, defaultValueMaster, minimumValue, maximumValue );
	}

	public int getCurrentGameStateValue() {
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
