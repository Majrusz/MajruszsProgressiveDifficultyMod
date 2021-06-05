package com.majruszs_difficulty.events.undead_army;

import com.majruszs_difficulty.config.GameStateDoubleConfig;
import com.majruszs_difficulty.config.GameStateIntegerConfig;
import com.mlib.config.*;

import static com.majruszs_difficulty.MajruszsDifficulty.CONFIG_HANDLER;

/** Class with all configurable aspects of the Undead Army. */
public class UndeadArmyConfig {
	private final ConfigGroup group;
	private final AvailabilityConfig availability;
	private final IntegerConfig killRequirement;
	private final DoubleConfig sizeMultiplier;
	private final GameStateIntegerConfig experienceReward;
	private final GameStateIntegerConfig treasureBagReward;
	private final GameStateDoubleConfig enchantedItemsChance;
	private final GameStateDoubleConfig armorChance;
	private final DurationConfig durationBetweenWaves;
	private final DurationConfig maximumInactiveDuration;

	public UndeadArmyConfig() {
		String availabilityComment = "Is the Undead Army enabled?";
		this.availability = new AvailabilityConfig( "is_enabled", availabilityComment, false, true );

		String killComment = "Required amount of killed undead to start the Undead Army.";
		this.killRequirement = new IntegerConfig( "kill_requirement", killComment, false, 100, 10, 1000 );

		String sizeComment = "Extra size multiplier for each extra player participating in the Undead Army.";
		this.sizeMultiplier = new DoubleConfig( "player_scale", sizeComment, false, 0.5, 0.1, 1.0 );

		String expComment = "Experience for each player after defeating the Undead Army.";
		this.experienceReward = new GameStateIntegerConfig( "Experience", expComment, 40, 80, 120, 4, 1000 );

		String bagComment = "Treasure Bags for each player after defeating the Undead Army.";
		this.treasureBagReward = new GameStateIntegerConfig( "TreasureBags", bagComment, 1, 1, 2, 1, 5 );

		String enchantComment = "Chance of the undead to have enchanted items. (separate for each item)";
		this.enchantedItemsChance = new GameStateDoubleConfig( "EnchantedItems", enchantComment, 0.125, 0.25, 0.5, 0.0, 1.0 );

		String armorComment = "Chance of the undead to have armor piece. (separate for each armor piece)";
		this.armorChance = new GameStateDoubleConfig( "ArmorChance", armorComment, 0.25, 0.5, 0.75, 0.0, 1.0 );

		String waveComment = "Time between waves. (in seconds) (requires game/world restart!) ";
		this.durationBetweenWaves = new DurationConfig( "time_between_waves", waveComment, true, 10.0, 3.0, 60.0 );

		String inactiveComment = "The maximum duration before Undead Army will end if there is no player. (in seconds) (requires game/world restart!) ";
		this.maximumInactiveDuration = new DurationConfig( "inactive_duration", inactiveComment, true, 900.0, 300.0, 3200.0 );

		this.group = CONFIG_HANDLER.addConfigGroup( new ConfigGroup( "UndeadArmy", "" ) );
		this.group.addConfigs( this.availability, this.killRequirement, this.sizeMultiplier, this.experienceReward, this.treasureBagReward,
			this.enchantedItemsChance, this.armorChance, this.durationBetweenWaves
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
		return 1.0 + this.sizeMultiplier.get() * ( Math.max( 1, amountOfPlayers ) - 1 );
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
		return this.enchantedItemsChance.getCurrentGameStateValue();
	}

	/** Returns a chance for undead to have a armor piece. (separately for each armor piece) */
	public double getArmorPieceChance() {
		return this.armorChance.getCurrentGameStateValue();
	}

	/** Returns amount of ticks between waves. */
	public int getAmountOfTicksBetweenWaves() {
		return this.durationBetweenWaves.getDuration();
	}

	/** Returns amount of ticks when the Undead Army will end if there is not any player nearby. */
	public int getAmountOfInactivityTicks() {
		return this.maximumInactiveDuration.getDuration();
	}
}
