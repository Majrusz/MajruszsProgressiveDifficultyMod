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
	private final ConfigGroup group;
	private final AvailabilityConfig availability;
	private final IntegerConfig killRequirement;
	private final DoubleConfig scaleWithPlayers;
	private final GameStateIntegerConfig experienceReward;
	private final GameStateIntegerConfig treasureBagReward;
	private final GameStateDoubleConfig enchantedItems;
	private final GameStateDoubleConfig armorChance;

	public UndeadArmyConfig() {
		String availabilityComment = "Is the Undead Army enabled?";
		String killComment = "Required amount of killed undead to start the Undead Army.";
		String scaleComment = "Undead Army size extra multiplier with each extra player.";
		String expComment = "Experience for each player after defeating the Undead Army.";
		String bagComment = "Treasure Bags for each player after defeating the Undead Army.";
		String enchantComment = "Chance of the undead to have enchanted items. (separate for each item)";
		String armorComment = "Chance of the undead to have armor piece. (separate for each armor piece)";
		this.availability = new AvailabilityConfig( "is_enabled", availabilityComment, false, true );
		this.killRequirement = new IntegerConfig( "kill_requirement", killComment, false, 100, 10, 1000 );
		this.scaleWithPlayers = new DoubleConfig( "player_scale", scaleComment, false, 0.5, 0.1, 1.0 );
		this.experienceReward = new GameStateIntegerConfig( "Experience", expComment, 40, 80, 120, 4, 1000 );
		this.treasureBagReward = new GameStateIntegerConfig( "TreasureBags", bagComment, 1, 1, 2, 1, 5 );
		this.enchantedItems = new GameStateDoubleConfig( "EnchantedItems", enchantComment, 0.125, 0.25, 0.5, 0.0, 1.0 );
		this.armorChance = new GameStateDoubleConfig( "ArmorChance", armorComment, 0.25, 0.5, 0.75, 0.0, 1.0 );

		this.group = CONFIG_HANDLER.addConfigGroup( new ConfigGroup( "UndeadArmy", "" ) );
		this.group.addConfigs( this.availability, this.killRequirement, this.scaleWithPlayers, this.experienceReward, this.treasureBagReward,
			this.enchantedItems, this.armorChance
		);
	}

	/** Returns whether Undead Army is enabled. */
	public boolean isUndeadArmyEnabled() {
		return this.availability.isEnabled();
	}

	/** Returns required amount of killed undead to start the Undead Army. */
	public int getRequiredKills() {
		return this.killRequirement.get();
	}

	/** Returns size multiplier depending on amount of players participating in. */
	public double getSizeMultiplier( int amountOfPlayers ) {
		return 1.0 + this.scaleWithPlayers.get() * ( Math.max( 1, amountOfPlayers ) - 1 );
	}

	/** Returns amount of experience as a reward for completing the Undead Army. */
	public int getAmountOfVictoryExperience() {
		return this.experienceReward.getCurrentGameStateValue();
	}

	/** Returns amount of Treasure Bags every player should get after victory. */
	public int getAmountOfVictoryTreasureBags() {
		return this.treasureBagReward.getCurrentGameStateValue();
	}

	/** Returns chance for undead to have a enchanted items instead of standard ones. (separately for each item) */
	public double getEnchantedItemChance() {
		return this.enchantedItems.getCurrentGameStateValue();
	}

	/** Returns a chance for undead to have a armor piece. (separately for each armor piece) */
	public double getArmorPieceChance() {
		return this.armorChance.getCurrentGameStateValue();
	}
}
