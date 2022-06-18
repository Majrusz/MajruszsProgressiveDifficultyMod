package com.majruszsdifficulty.features;

import com.majruszsdifficulty.GameState;
import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;

import static com.majruszsdifficulty.MajruszsDifficulty.FEATURES_GROUP;

/** Class representing base feature that can be disabled. */
public abstract class FeatureBase {
	protected final GameState.State minimumState;
	protected final ConfigGroup featureGroup;
	protected final AvailabilityConfig availability;

	public FeatureBase( String configName, String configComment, GameState.State minimumState ) {
		this.minimumState = minimumState;

		String enabledComment = "Is this feature enabled?";
		this.availability = new AvailabilityConfig( "is_enabled", enabledComment, false, true );

		this.featureGroup = FEATURES_GROUP.addGroup( new ConfigGroup( configName, configComment ) );
		this.featureGroup.addConfigs( this.availability );
	}

	/** Checking if event is not disabled by the player. */
	public boolean isEnabled() {
		return this.availability.isEnabled();
	}
}
