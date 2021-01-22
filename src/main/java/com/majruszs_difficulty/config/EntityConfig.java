package com.majruszs_difficulty.config;

import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.IConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import static com.majruszs_difficulty.MajruszsDifficulty.ENTITIES_GROUP;

/** Handling config for a single entity. */
public class EntityConfig implements IConfig {
	public final ConfigGroup entityGroup;
	public final AvailabilityConfig availability;

	public EntityConfig( String entityName ) {
		String spawn_comment = "Is entity naturally spawning?";
		this.availability = new AvailabilityConfig( "is_spawning", spawn_comment, false, true );

		this.entityGroup = ENTITIES_GROUP.addGroup( new ConfigGroup( entityName, "" ) );
		this.entityGroup.addConfigs( this.availability );
	}

	@Override
	public void build( ForgeConfigSpec.Builder builder ) {
		this.entityGroup.build( builder );
	}
}
