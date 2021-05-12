package com.majruszsdifficulty.events.undead_army;

import com.majruszsdifficulty.config.GameStateDoubleConfig;
import com.majruszsdifficulty.config.GameStateIntegerConfig;
import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.config.IntegerConfig;

import static com.majruszsdifficulty.MajruszsDifficulty.CONFIG_HANDLER;

/** All possible configurations for Undead Army. */
public class UndeadArmyConfig {
	public final ConfigGroup group;
	public final AvailabilityConfig availability;
	public final IntegerConfig killRequirement;
	public final DoubleConfig scaleWithPlayers;
	public final GameStateIntegerConfig experienceReward;
	public final GameStateIntegerConfig treasureBagReward;
	public final GameStateDoubleConfig enchantedItems;
	public final GameStateDoubleConfig armorChance;

	public UndeadArmyConfig() {
		this.group = CONFIG_HANDLER.addConfigGroup( new ConfigGroup( "UndeadArmy", "" ) );

		String availabilityComment = "Is Undead Army enabled?";
		String killComment = "Amount of undead required to kill to start Undead Army.";
		String scaleComment = "Undead Army size extra multiplier with each extra player.";
		String expComment = "Experience for each player after defeating Undead Army.";
		String bagComment = "Treasure Bags for each player after defeating Undead Army.";
		String enchantComment = "Chance for entities to have enchanted items.";
		String armorComment = "Chance for entities to have a armor piece. Separate chance for each piece.";
		this.availability = new AvailabilityConfig( "is_enabled", availabilityComment, false, true );
		this.killRequirement = new IntegerConfig( "kill_requirement", killComment, false, 100, 10, 1000 );
		this.scaleWithPlayers = new DoubleConfig( "player_scale", scaleComment, false, 0.5, 0.1, 1.0 );
		this.experienceReward = new GameStateIntegerConfig( "experience", expComment, 40, 80, 120, 4, 1000 );
		this.treasureBagReward = new GameStateIntegerConfig( "treasure_bags", bagComment, 1, 1, 2, 1, 5 );
		this.enchantedItems = new GameStateDoubleConfig( "enchanted_items", enchantComment, 0.125, 0.25, 0.5, 0.0, 1.0 );
		this.armorChance = new GameStateDoubleConfig( "armor_chance", armorComment, 0.25, 0.5, 0.75, 0.0, 1.0 );
		this.group.addConfigs( this.availability, this.killRequirement, this.scaleWithPlayers, this.experienceReward, this.treasureBagReward,
			this.enchantedItems, this.armorChance
		);
	}
}
