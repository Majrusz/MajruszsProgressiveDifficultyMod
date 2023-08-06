package com.majruszsdifficulty.config;

import com.mlib.config.ConfigGroup;
import com.mlib.config.StringListConfig;
import net.minecraft.resources.ResourceLocation;

public class StageProgressConfig extends ConfigGroup {
	final StringListConfig triggeringEntities;
	final StringListConfig triggeringDimensions;

	public StageProgressConfig( String defaultEntity, String defaultDimension ) {
		this.triggeringEntities = new StringListConfig( defaultEntity );
		this.triggeringDimensions = new StringListConfig( defaultDimension );

		this.addConfig( this.triggeringEntities.name( "triggering_entities" )
			.comment( "List of entities which start the game stage after killing them." )
		).addConfig( this.triggeringDimensions.name( "triggering_dimensions" )
			.comment( "List of dimensions which start the game stage after entering them." )
		);
	}

	public boolean entityTriggersChange( ResourceLocation entityLocation ) {
		return this.triggeringEntities.contains( entityLocation.toString() );
	}

	public boolean dimensionTriggersChange( ResourceLocation dimensionLocation ) {
		return this.triggeringDimensions.contains( dimensionLocation.toString() );
	}
}
