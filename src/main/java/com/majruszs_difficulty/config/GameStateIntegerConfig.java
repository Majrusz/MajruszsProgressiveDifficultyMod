package com.majruszs_difficulty.config;

import com.majruszs_difficulty.GameState;
import com.mlib.config.ConfigGroup;
import com.mlib.config.IConfig;
import com.mlib.config.IntegerConfig;
import net.minecraftforge.common.ForgeConfigSpec;

/** Config with integer value depending on current game state. */
public class GameStateIntegerConfig implements IConfig {
	protected final ConfigGroup group;
	protected final IntegerConfig normal;
	protected final IntegerConfig expert;
	protected final IntegerConfig master;

	public GameStateIntegerConfig( String name, String comment, int defaultValueNormal, int defaultValueExpert, int defaultValueMaster,
		int minimumValue, int maximumValue
	) {
		this.group = new ConfigGroup( name, comment );
		this.normal = new IntegerConfig( "normal", "Normal Mode", false, defaultValueNormal, minimumValue, maximumValue );
		this.expert = new IntegerConfig( "expert", "Expert Mode", false, defaultValueExpert, minimumValue, maximumValue );
		this.master = new IntegerConfig( "master", "Master Mode", false, defaultValueMaster, minimumValue, maximumValue );
		this.group.addConfigs( this.normal, this.expert, this.master );
	}

	/** Returns integer value depending on current game state. */
	public int getCurrentGameStateValue() {
		return GameState.getValueDependingOnCurrentGameState( this.normal.get(), this.expert.get(), this.master.get() );
	}

	@Override
	public void build( ForgeConfigSpec.Builder builder ) {
		this.group.build( builder );
	}
}
