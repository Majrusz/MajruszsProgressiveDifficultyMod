package com.majruszs_difficulty.config;

import com.mlib.config.ConfigGroup;
import com.mlib.config.IConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import static com.majruszs_difficulty.MajruszsDifficulty.ENTITIES_GROUP;

/** Base for all mob configs. */
public class MobConfig implements IConfig {
	public final ConfigGroup entityGroup;

	public MobConfig( String entityName ) {
		this.entityGroup = ENTITIES_GROUP.addGroup( new ConfigGroup( entityName, "" ) );
	}

	@Override
	public void build( ForgeConfigSpec.Builder builder ) {
		this.entityGroup.build( builder );
	}
}
