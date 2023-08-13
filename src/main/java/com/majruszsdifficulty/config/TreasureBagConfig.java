package com.majruszsdifficulty.config;

import com.mlib.config.BooleanConfig;
import com.mlib.config.ConfigGroup;

public class TreasureBagConfig extends ConfigGroup {
	final BooleanConfig availability = new BooleanConfig( true );

	public TreasureBagConfig( String name ) {
		this.addConfig( this.availability.name( "is_enabled" ).comment( "Determines whether this Treasure Bag should drop." ) );
		this.name( name );
	}

	public boolean isEnabled() {
		return this.availability.isEnabled();
	}
}
