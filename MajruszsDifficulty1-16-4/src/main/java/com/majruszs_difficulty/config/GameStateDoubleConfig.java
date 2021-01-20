package com.majruszs_difficulty.config;

import com.majruszs_difficulty.GameState;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.config.IConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.security.acl.Group;

public class GameStateDoubleConfig implements IConfig {
	protected final ConfigGroup group;
	protected final DoubleConfig normal;
	protected final DoubleConfig expert;
	protected final DoubleConfig master;

	public GameStateDoubleConfig( String name, String comment, double defaultValueNormal, double defaultValueExpert, double defaultValueMaster, double minimumValue, double maximumValue ) {
		this.group = new ConfigGroup( name, comment );
		this.normal = new DoubleConfig( "normal", "Normal Mode", false, defaultValueNormal, minimumValue, maximumValue );
		this.expert = new DoubleConfig( "expert", "Expert Mode", false, defaultValueExpert, minimumValue, maximumValue );
		this.master = new DoubleConfig( "master", "Master Mode", false, defaultValueMaster, minimumValue, maximumValue );
		this.group.addConfigs( this.normal, this.expert, this.master );
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
		this.group.build( builder );
	}
}
