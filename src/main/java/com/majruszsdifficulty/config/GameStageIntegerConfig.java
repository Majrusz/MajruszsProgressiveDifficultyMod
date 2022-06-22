package com.majruszsdifficulty.config;

import com.majruszsdifficulty.GameStage;
import com.mlib.config.ConfigGroup;
import com.mlib.config.IntegerConfig;
import com.mlib.config.UserConfig;
import net.minecraftforge.common.ForgeConfigSpec;

public class GameStageIntegerConfig extends UserConfig {
	protected final ConfigGroup group;
	protected final IntegerConfig normal;
	protected final IntegerConfig expert;
	protected final IntegerConfig master;

	public GameStageIntegerConfig( String name, String comment, int defaultNormal, int defaultExpert, int defaultMaster, int min, int max ) {
		super( name, comment );
		this.normal = new IntegerConfig( "normal", "Normal Mode", false, defaultNormal, min, max );
		this.expert = new IntegerConfig( "expert", "Expert Mode", false, defaultExpert, min, max );
		this.master = new IntegerConfig( "master", "Master Mode", false, defaultMaster, min, max );
		this.group = new ConfigGroup( name, comment, this.normal, this.expert, this.master );
	}

	public int getCurrentGameStageValue() {
		return GameStage.getCurrentGameStageDependentValue( this.normal.get(), this.expert.get(), this.master.get() );
	}

	@Override
	public void build( ForgeConfigSpec.Builder builder ) {
		this.group.build( builder );
	}

	public Integer get() {
		return getCurrentGameStageValue();
	}
}
