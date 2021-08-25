package com.majruszs_difficulty;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

/** Stores information about current game state in level. */
public class GameDataSaver extends SavedData {
	public static final String DATA_NAME = MajruszsDifficulty.MOD_ID;
	private static final String COMPOUND_STATE_TAG = "MajruszsDifficultyCompound";
	private static final String DIFFICULTY_STATE_TAG = "DifficultyState";
	private CompoundTag DATA = new CompoundTag();

	public GameDataSaver( boolean loadDefaultStateFromConfig ) {
		if( loadDefaultStateFromConfig )
			GameState.changeMode( Instances.GAME_STATE_CONFIG.getDefaultState() );
	}

	public GameDataSaver() {
		this( true );
	}

	@Override
	public CompoundTag save( CompoundTag nbt ) {
		this.DATA.putInt( DIFFICULTY_STATE_TAG, GameState.convertStateToInteger( GameState.getCurrentMode() ) );

		nbt.put( COMPOUND_STATE_TAG, this.DATA );
		return nbt;
	}

	public static GameDataSaver load( CompoundTag nbt ) {
		GameDataSaver gameData = new GameDataSaver( false );
		gameData.DATA = nbt.getCompound( COMPOUND_STATE_TAG );
		gameData.updateGameState();

		return gameData;
	}

	public void updateGameState() {
		GameState.changeMode( GameState.convertIntegerToState( this.DATA.getInt( DIFFICULTY_STATE_TAG ) ) );
	}
}
