package com.majruszsdifficulty.config;

import com.majruszsdifficulty.GameStage;
import com.mlib.config.ConfigGroup;
import com.mlib.config.IValueConfig;
import com.mlib.config.StringListConfig;
import com.mlib.config.UserConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class GameStageStringListConfig extends UserConfig implements IValueConfig< List< ? extends String > > {
	protected final ConfigGroup group;
	protected final StringListConfig normal;
	protected final StringListConfig expert;
	protected final StringListConfig master;

	public GameStageStringListConfig( String name, String comment, String[] defaultNormal, String[] defaultExpert, String[] defaultMaster ) {
		super( name, comment );
		this.normal = new StringListConfig( "normal", "Normal Mode", false, defaultNormal );
		this.expert = new StringListConfig( "expert", "Expert Mode", false, defaultExpert );
		this.master = new StringListConfig( "master", "Master Mode", false, defaultMaster );
		this.group = new ConfigGroup( name, comment, this.normal, this.expert, this.master );
	}

	public List< ? extends String > getCurrentGameStageValue() {
		return GameStage.getCurrentGameStageDependentValue( this.normal.get(), this.expert.get(), this.master.get() );
	}

	@Override
	public void build( ForgeConfigSpec.Builder builder ) {
		this.group.build( builder );
	}

	@Override
	public List< ? extends String > get() {
		return getCurrentGameStageValue();
	}
}
