package com.majruszsdifficulty.undeadarmy;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.config.GameStageDoubleConfig;
import com.majruszsdifficulty.config.GameStageIntegerConfig;
import com.mlib.MajruszLibrary;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.BooleanConfig;
import com.mlib.config.DoubleConfig;
import com.mlib.config.IntegerConfig;
import com.mlib.math.Range;

import java.util.List;

@AutoInstance
public class UndeadArmyConfig {
	static final BooleanConfig ENABLED = new BooleanConfig( true );
	static final IntegerConfig KILL_REQUIREMENT = new IntegerConfig( 100, new Range<>( 0, 1000 ) );
	static final DoubleConfig SIZE_MULTIPLIER = new DoubleConfig( 0.5, new Range<>( 0.1, 1.0 ) );
	static final DoubleConfig SKELETON_HORSE_CHANCE = new DoubleConfig( 0.15, Range.CHANCE );
	static final GameStageIntegerConfig EXPERIENCE_REWARD = new GameStageIntegerConfig( 40, 80, 120, new Range<>( 4, 1000 ) );
	static final GameStageDoubleConfig ENCHANTED_ITEM_CHANCE = new GameStageDoubleConfig( 0.125, 0.25, 0.5, Range.CHANCE );
	static final GameStageDoubleConfig ARMOR_CHANCE = new GameStageDoubleConfig( 0.25, 0.5, 0.75, Range.CHANCE );
	static final DoubleConfig DURATION_BETWEEN_WAVES = new DoubleConfig( 10.0, new Range<>( 3.0, 60.0 ) );
	static final DoubleConfig MAXIMUM_INACTIVE_DURATION = new DoubleConfig( 900.0, new Range<>( 300.0, 3200.0 ) );
	static final WaveMembersConfig WAVE_MEMBERS = new WaveMembersConfig()
		.addWaveConfig( "4-6 minecraft:zombie", "1-2 minecraft:husk", "2-4 minecraft:skeleton", "1-2 minecraft:stray" )
		.addWaveConfig( "3-5 minecraft:zombie", "1-2 minecraft:husk", "2-4 minecraft:skeleton", "1-2 minecraft:stray" )
		.addWaveConfig( "2-4 minecraft:zombie", "1-3 minecraft:husk", "1-3 minecraft:skeleton", "1-3 minecraft:stray", "1-1 majruszsdifficulty:tank" )
		.addWaveConfig( "1-3 minecraft:zombie", "2-4 minecraft:husk", "1-3 minecraft:skeleton", "2-4 minecraft:stray", "1-1 minecraft:wither_skeleton", "2-2 majruszsdifficulty:tank" )
		.addWaveConfig( "1-3 minecraft:zombie", "3-5 minecraft:husk", "1-3 minecraft:skeleton", "3-5 minecraft:stray", "2-3 minecraft:wither_skeleton", "3-3 majruszsdifficulty:tank" );


	public UndeadArmyConfig() {
		MajruszLibrary.MOD_CONFIGS.get( Registries.Modifiers.UNDEAD_ARMY )
			.addConfig( ENABLED
				.name( "is_enabled" )
				.comment( "Can the Undead Army spawn?" )
			).addConfig( KILL_REQUIREMENT
				.name( "kill_requirement" )
				.comment( "Required amount of killed undead to start the Undead Army. (set to 0 if you want to disable this)" )
			).addConfig( SIZE_MULTIPLIER
				.name( "player_scale" )
				.comment( "Extra size multiplier for each extra player participating in the Undead Army." )
			).addConfig( SKELETON_HORSE_CHANCE
				.name( "horse_chance" )
				.comment( "Chance for all Skeletons to spawn on Skeleton Horse." )
			).addConfig( EXPERIENCE_REWARD
				.name( "ExperienceReward" )
				.comment( "Experience reward for each player after defeating the Undead Army." )
			).addConfig( ENCHANTED_ITEM_CHANCE
				.name( "EnchantedItemChance" )
				.comment( "Chance for the undead item to be enchanted (separate for each item)." )
			).addConfig( ARMOR_CHANCE
				.name( "ArmorChance" )
				.comment( "Chance for the undead to have armor piece (separate for each armor piece)." )
			).addConfig( DURATION_BETWEEN_WAVES
				.name( "time_between_waves" )
				.comment( "Duration between waves (in seconds)." )
				.requiresWorldRestart( true )
			).addConfig( MAXIMUM_INACTIVE_DURATION
				.name( "inactive_duration" )
				.comment( "Duration after which the Undead Army will end if there is no player nearby (in seconds)." )
				.requiresWorldRestart( true )
			).addConfig( WAVE_MEMBERS
				.name( "WaveMembers" )
				.comment( "Amount of enemies in each wave (format: {minimal amount}-{maximal amount} {entity})." )
			);
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
