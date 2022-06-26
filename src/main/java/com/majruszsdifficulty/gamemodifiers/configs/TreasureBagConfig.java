package com.majruszsdifficulty.gamemodifiers.configs;

import com.mlib.config.BooleanConfig;
import com.mlib.config.ConfigGroup;

public class TreasureBagConfig extends ConfigGroup {
	final BooleanConfig availability;

	public TreasureBagConfig( String groupName, String groupComment ) {
		super( groupName, groupComment );
		this.availability = new BooleanConfig( "is_enabled", "Determines whether this Treasure Bag should drop.", false, true );
		this.addConfig( this.availability );
	}

	public boolean isEnabled() {
		return this.availability.isEnabled();
	}
}
