package com.majruszsdifficulty.config;

import com.majruszsdifficulty.GameState;
import com.mlib.config.ConfigGroup;
import com.mlib.config.IConfigType;
import com.mlib.config.IntegerConfig;
import net.minecraftforge.common.ForgeConfigSpec;

/** Config with integer values for each game state. */
public class GameStateIntegerConfig implements IConfigType< Integer > {
	protected final ConfigGroup group;
	protected final IntegerConfig normal;
	protected final IntegerConfig expert;
	protected final IntegerConfig master;

	public GameStateIntegerConfig( String name, String comment, int defaultValueNormal, int defaultValueExpert, int defaultValueMaster, int minimumValue,
		int maximumValue
	) {
		this.normal = new IntegerConfig( "normal", "Normal Mode", false, defaultValueNormal, minimumValue, maximumValue );
		this.expert = new IntegerConfig( "expert", "Expert Mode", false, defaultValueExpert, minimumValue, maximumValue );
		this.master = new IntegerConfig( "master", "Master Mode", false, defaultValueMaster, minimumValue, maximumValue );
		this.group = new ConfigGroup( name, comment, this.normal, this.expert, this.master );
	}

	public int getCurrentGameStateValue() {
		return GameState.getCurrentGameStateValue( this.normal.get(), this.expert.get(), this.master.get() );
	}

	@Override
	public void build( ForgeConfigSpec.Builder builder ) {
		this.group.build( builder );
	}

	@Override
	public Integer get() {
		return getCurrentGameStateValue();
	}
}
