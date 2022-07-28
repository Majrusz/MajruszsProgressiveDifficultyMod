package com.majruszsdifficulty.config;

import com.majruszsdifficulty.GameStage;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.config.IValueConfig;
import com.mlib.config.UserConfig;
import net.minecraftforge.common.ForgeConfigSpec;

public class GameStageDoubleConfig extends UserConfig implements IValueConfig< Double > {
	protected final ConfigGroup group;
	protected final DoubleConfig normal;
	protected final DoubleConfig expert;
	protected final DoubleConfig master;

	public GameStageDoubleConfig( String name, String comment, double defaultNormal, double defaultExpert, double defaultMaster, double min, double max ) {
		super( name, comment );
		this.normal = new DoubleConfig( "normal", "Normal Mode", false, defaultNormal, min, max );
		this.expert = new DoubleConfig( "expert", "Expert Mode", false, defaultExpert, min, max );
		this.master = new DoubleConfig( "master", "Master Mode", false, defaultMaster, min, max );
		this.group = new ConfigGroup( name, comment, this.normal, this.expert, this.master );
	}

	public double getCurrentGameStageValue() {
		return GameStage.getCurrentGameStageDependentValue( this.normal.get(), this.expert.get(), this.master.get() );
	}

	@Override
	public void build( ForgeConfigSpec.Builder builder ) {
		this.group.build( builder );
	}

	@Override
	public Double get() {
		return getCurrentGameStageValue();
	}
}
