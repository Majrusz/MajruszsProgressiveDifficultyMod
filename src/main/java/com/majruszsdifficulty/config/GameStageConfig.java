package com.majruszsdifficulty.config;

import com.majruszsdifficulty.GameStage;
import com.mlib.config.ConfigGroup;
import com.mlib.config.IConfigurable;
import com.mlib.config.ValueConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Supplier;

public class GameStageConfig< Type > implements IConfigurable, Supplier< Type > {
	final ValueConfig< Type > normal, expert, master;
	final ConfigGroup group;

	public GameStageConfig( String name, String comment, ValueConfig< Type > normal, ValueConfig< Type > expert, ValueConfig< Type > master ) {
		this.normal = normal;
		this.expert = expert;
		this.master = master;
		this.group = new ConfigGroup( name, comment, this.normal, this.expert, this.master );
	}

	public Type getCurrentGameStageValue() {
		return GameStage.getCurrentGameStageDependentValue( this.normal.get(), this.expert.get(), this.master.get() );
	}

	@Override
	public String getName() {
		return this.group.getName();
	}

	@Override
	public String getComment() {
		return this.group.getComment();
	}

	@Override
	public void build( ForgeConfigSpec.Builder builder ) {
		this.group.build( builder );
	}

	@Override
	public Type get() {
		return getCurrentGameStageValue();
	}
}
