package com.majruszsdifficulty.gamemodifiers.configs;

import com.mlib.config.ConfigGroup;
import com.mlib.config.StringListConfig;
import net.minecraft.resources.ResourceLocation;

public class StageProgressConfig extends ConfigGroup {
	final StringListConfig triggeringEntities;
	final StringListConfig triggeringDimensions;

	public StageProgressConfig( String groupName, String groupComment, String defaultEntity, String defaultDimension ) {
		super( groupName, groupComment );
		this.triggeringEntities = new StringListConfig( "triggering_entities", "List of entities which start the game stage after killing them.", false, defaultEntity );
		this.triggeringDimensions = new StringListConfig( "triggering_dimensions", "List of dimensions which start the game stage after entering them.", false, defaultDimension );
		this.addConfigs( this.triggeringEntities, this.triggeringDimensions );
	}

	public boolean entityTriggersChange( ResourceLocation entityLocation ) {
		return this.triggeringEntities.contains( entityLocation.toString() );
	}

	public boolean dimensionTriggersChange( ResourceLocation dimensionLocation ) {
		return this.triggeringDimensions.contains( dimensionLocation.toString() );
	}
}
