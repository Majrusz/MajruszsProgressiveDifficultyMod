package com.majruszsdifficulty.events;

import com.majruszsdifficulty.GameState;
import com.mlib.WorldHelper;
import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import net.minecraft.entity.LivingEntity;

import static com.majruszsdifficulty.MajruszsDifficulty.FEATURES_GROUP;

/** Class representing base feature that depends on chance and can be disabled. */
public abstract class FeatureBase {
	protected final GameState.State minimumState;
	protected final boolean shouldChanceBeMultipliedByCRD; // CRD = Clamped Regional Difficulty
	protected final ConfigGroup featureGroup;
	protected final AvailabilityConfig availability;
	protected final DoubleConfig chance;

	public FeatureBase( String configName, String configComment, double defaultChance, GameState.State minimumState,
		boolean shouldChanceBeMultipliedByCRD
	) {
		this.minimumState = minimumState;
		this.shouldChanceBeMultipliedByCRD = shouldChanceBeMultipliedByCRD;

		String crd = "(this value is scaled by Clamped Regional Difficulty)";
		String enabled_comment = "Is this feature enabled?";
		String chance_comment = "Chance of this feature to happen. " + ( shouldChanceBeMultipliedByCRD ? crd : "" );
		this.availability = new AvailabilityConfig( "is_enabled", enabled_comment, false, true );
		this.chance = new DoubleConfig( "chance", chance_comment, false, defaultChance, 0.0, 1.0 );

		this.featureGroup = FEATURES_GROUP.addGroup( new ConfigGroup( configName, configComment ) );
		this.featureGroup.addConfigs( this.availability, this.chance );
	}

	/** Checking if event is not disabled by the player. */
	protected boolean isEnabled() {
		return this.availability.isEnabled();
	}

	/** Returns chance of applying event on entity. */
	protected double getChance() {
		return this.chance.get();
	}

	/** Calculating final chance. (after applying clamped regional difficulty if needed) */
	protected double calculateChance( LivingEntity target ) {
		return getChance() * ( this.shouldChanceBeMultipliedByCRD ? WorldHelper.getClampedRegionalDifficulty( target ) : 1.0 );
	}
}
