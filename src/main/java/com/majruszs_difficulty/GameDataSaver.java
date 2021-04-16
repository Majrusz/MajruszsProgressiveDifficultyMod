package com.majruszs_difficulty;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;

/** Saving current data like game state to world data. */
public class GameDataSaver extends WorldSavedData {
	public static final String DATA_NAME = MajruszsDifficulty.MOD_ID;
	private static final String COMPOUND_STATE_TAG = "MajruszsDifficultyCompound";
	private static final String DIFFICULTY_STATE_TAG = "DifficultyState";
	private CompoundNBT DATA = new CompoundNBT();

	public GameDataSaver() {
		super( DATA_NAME );
	}

	@Override
	public void read( CompoundNBT nbt ) {
		this.DATA = nbt.getCompound( COMPOUND_STATE_TAG );

		updateGameState();
	}

	@Override
	public CompoundNBT write( CompoundNBT nbt ) {
		this.DATA.putInt( DIFFICULTY_STATE_TAG, GameState.convertStateToInteger( GameState.getCurrentMode() ) );

		nbt.put( COMPOUND_STATE_TAG, this.DATA );
		return nbt;
	}

	public void updateGameState() {
		GameState.changeMode( GameState.convertIntegerToState( this.DATA.getInt( DIFFICULTY_STATE_TAG ) ) );
	}
}
