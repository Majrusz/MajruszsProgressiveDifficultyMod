package com.majruszs_difficulty.events.undead_army;

import com.majruszs_difficulty.config.GameStateDoubleConfig;
import com.majruszs_difficulty.config.GameStateIntegerConfig;
import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.config.IntegerConfig;

import static com.majruszs_difficulty.MajruszsDifficulty.CONFIG_HANDLER;

/** All possible configurations for Undead Army. */
public class UndeadArmyConfig {
	public final ConfigGroup group;
	public final AvailabilityConfig availability;
	public final IntegerConfig killRequirement;
	public final DoubleConfig scaleWithPlayers;
	public final GameStateIntegerConfig experienceReward;
	public final GameStateIntegerConfig treasureBagReward;
	public final GameStateDoubleConfig enchantedItems;

	public UndeadArmyConfig() {
		this.group = CONFIG_HANDLER.addConfigGroup( new ConfigGroup( "UndeadArmy", "" ) );

		String availability_comment = "Is Undead Army enabled?";
		String kill_comment = "Amount of undead required to kill at night to start Undead Army.";
		String scale_comment = "Undead Army size extra multiplier with each extra player.";
		String exp_comment = "Experience for each player after defeating Undead Army.";
		String bag_comment = "Treasure Bags for each player after defeating Undead Army.";
		String enchant_comment = "Chance for entities to have enchanted items.";
		this.availability = new AvailabilityConfig( "is_enabled", availability_comment, false, true );
		this.killRequirement = new IntegerConfig( "kill_requirement", kill_comment, false, 50, 10, 1000 );
		this.scaleWithPlayers = new DoubleConfig( "player_scale", scale_comment, false, 0.5, 0.1, 1.0 );
		this.experienceReward = new GameStateIntegerConfig( "experience", exp_comment, 40, 80, 120, 4, 1000 );
		this.treasureBagReward = new GameStateIntegerConfig( "treasure_bags", bag_comment, 1, 1, 2, 1, 5 );
		this.enchantedItems = new GameStateDoubleConfig( "enchanted_items", enchant_comment, 0.125, 0.25, 0.5, 0.0, 1.0 );
		this.group.addConfigs( this.availability, this.killRequirement, this.scaleWithPlayers, this.experienceReward, this.treasureBagReward,
			this.enchantedItems
		);
	}
}
