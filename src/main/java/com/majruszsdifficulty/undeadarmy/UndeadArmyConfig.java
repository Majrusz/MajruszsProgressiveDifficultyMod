package com.majruszsdifficulty.undeadarmy;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.config.GameStageDoubleConfig;
import com.majruszsdifficulty.config.GameStageIntegerConfig;
import com.mlib.config.BooleanConfig;
import com.mlib.config.DoubleConfig;
import com.mlib.config.IntegerConfig;

import java.util.List;

import static com.majruszsdifficulty.Registries.UNDEAD_ARMY_GROUP;

public class UndeadArmyConfig {
	static final BooleanConfig ENABLED = new BooleanConfig( "is_enabled", "Can the Undead Army spawn?", false, true );
	static final IntegerConfig KILL_REQUIREMENT = new IntegerConfig( "kill_requirement", "Required amount of killed undead to start the Undead Army. (set to 0 if you want to disable this)", false, 100, 0, 1000 );
	static final DoubleConfig SIZE_MULTIPLIER = new DoubleConfig( "player_scale", "Extra size multiplier for each extra player participating in the Undead Army.", false, 0.5, 0.1, 1.0 );
	static final DoubleConfig SKELETON_HORSE_CHANCE = new DoubleConfig( "horse_chance", "Chance for all Skeletons to spawn on Skeleton Horse.", false, 0.15, 0.1, 1.0 );
	static final GameStageIntegerConfig EXPERIENCE_REWARD = new GameStageIntegerConfig( "ExperienceReward", "Experience reward for each player after defeating the Undead Army.", 40, 80, 120, 4, 1000 );
	static final GameStageDoubleConfig ENCHANTED_ITEM_CHANCE = new GameStageDoubleConfig( "EnchantedItemChance", "Chance for the undead item to be enchanted (separate for each item).", 0.125, 0.25, 0.5, 0.0, 1.0 );
	static final GameStageDoubleConfig ARMOR_CHANCE = new GameStageDoubleConfig( "ArmorChance", "Chance for the undead to have armor piece (separate for each armor piece).", 0.25, 0.5, 0.75, 0.0, 1.0 );
	static final DoubleConfig DURATION_BETWEEN_WAVES = new DoubleConfig( "time_between_waves", "Time in seconds between waves.", true, 10.0, 3.0, 60.0 );
	static final DoubleConfig MAXIMUM_INACTIVE_DURATION = new DoubleConfig( "inactive_duration", "Duration in seconds after which the Undead Army will end if there is no player nearby.", true, 900.0, 300.0, 3200.0 );
	static final WaveMembersConfig WAVE_MEMBERS = new WaveMembersConfig( "WaveMembers", "Amount of enemies in each wave (format: {minimal amount}-{maximal amount} {entity})." );

	static {
		WAVE_MEMBERS.addWaveConfig( "4-6 minecraft:zombie", "1-2 minecraft:husk", "2-4 minecraft:skeleton", "1-2 minecraft:stray" );
		WAVE_MEMBERS.addWaveConfig( "3-5 minecraft:zombie", "1-2 minecraft:husk", "2-4 minecraft:skeleton", "1-2 minecraft:stray" );
		WAVE_MEMBERS.addWaveConfig( "2-4 minecraft:zombie", "1-3 minecraft:husk", "1-3 minecraft:skeleton", "1-3 minecraft:stray", "1-1 majruszsdifficulty:tank" );
		WAVE_MEMBERS.addWaveConfig( "1-3 minecraft:zombie", "3-5 minecraft:husk", "1-3 minecraft:skeleton", "2-4 minecraft:stray", "2-2 majruszsdifficulty:tank" );
		WAVE_MEMBERS.addWaveConfig( "1-3 minecraft:zombie", "4-6 minecraft:husk", "1-3 minecraft:skeleton", "3-5 minecraft:stray", "3-3 majruszsdifficulty:tank" );
		UNDEAD_ARMY_GROUP.addConfigs( ENABLED, KILL_REQUIREMENT, SIZE_MULTIPLIER, SKELETON_HORSE_CHANCE, EXPERIENCE_REWARD, ENCHANTED_ITEM_CHANCE, ARMOR_CHANCE, DURATION_BETWEEN_WAVES, MAXIMUM_INACTIVE_DURATION, WAVE_MEMBERS );
	}

	public static boolean isEnabled() {
		return ENABLED.get();
	}

	public static int getRequiredKills() {
		return KILL_REQUIREMENT.get();
	}

	public static double getSizeMultiplier( int amountOfPlayers ) {
		return 1.0 + SIZE_MULTIPLIER.get() * ( Math.max( 1, amountOfPlayers ) - 1 );
	}

	public static double getSkeletonHorseChance() {
		return SKELETON_HORSE_CHANCE.get();
	}

	public static int getAmountOfVictoryExperience() {
		return EXPERIENCE_REWARD.getCurrentGameStageValue();
	}

	public static double getEnchantedItemChance() {
		return ENCHANTED_ITEM_CHANCE.getCurrentGameStageValue();
	}

	public static double getArmorPieceChance() {
		return ARMOR_CHANCE.getCurrentGameStageValue();
	}

	public static int getTicksBetweenWaves() {
		return DURATION_BETWEEN_WAVES.asTicks();
	}

	public static int getInactivityTicks() {
		return MAXIMUM_INACTIVE_DURATION.asTicks();
	}

	public static int getWavesCount() {
		return GameStage.getCurrentGameStageDependentValue( 3, 4, 5 );
	}

	public static List< WaveMembersConfig.WaveMember > getWaveMembers( int waveNumber ) {
		return WAVE_MEMBERS.getWaveMembers( waveNumber );
	}

}
