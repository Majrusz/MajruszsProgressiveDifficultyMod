package com.majruszs_difficulty.events.undead_army;

import com.majruszs_difficulty.events.UndeadArmy;
import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.config.IntegerConfig;

import static com.majruszs_difficulty.MajruszsDifficulty.CONFIG_HANDLER;

public class UndeadArmyConfig {
	public final ConfigGroup group;
	public final AvailabilityConfig availability;
	public final IntegerConfig killRequirement;
	public final DoubleConfig scaleWithPlayers;

	public UndeadArmyConfig() {
		this.group = CONFIG_HANDLER.addConfigGroup( new ConfigGroup( "UndeadArmy", "" ) );

		String availability_comment = "Is Undead Army enabled?";
		String kill_comment = "Amount of undead required to kill at night to start Undead Army.";
		String scale_comment = "Undead Army size extra multiplier with each extra player.";
		this.availability = new AvailabilityConfig( "is_enabled", availability_comment, false, true );
		this.killRequirement = new IntegerConfig( "kill_requirement", kill_comment, false, 50, 10, 1000 );
		this.scaleWithPlayers = new DoubleConfig( "player_scale", scale_comment, false, 0.5, 0.1, 1.0 );
		this.group.addConfigs( this.availability, this.killRequirement, this.scaleWithPlayers );
	}
}
