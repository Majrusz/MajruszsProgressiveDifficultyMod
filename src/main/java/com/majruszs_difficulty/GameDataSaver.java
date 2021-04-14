package com.majruszs_difficulty;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;

/** Saving current data like current game state. */
public class GameDataSaver extends WorldSavedData {
	public static final String DATA_NAME = MajruszsDifficulty.MOD_ID;
	private CompoundNBT data = new CompoundNBT();

	public GameDataSaver() {
		super( DATA_NAME );
	}

	@Override
	public void load( CompoundNBT nbt ) {
		this.data = nbt.getCompound( "MajruszsDifficultyCompound" );

		updateGameState();
	}

	@Override
	public CompoundNBT save( CompoundNBT nbt ) {
		this.data.putInt( "DifficultyState", GameState.convertStateToInteger( GameState.getCurrentMode() ) );

		nbt.put( "MajruszsDifficultyCompound", this.data );
		return nbt;
	}

	public void updateGameState() {
		GameState.changeMode( GameState.convertIntegerToState( this.data.getInt( "DifficultyState" ) ) );
	}
}
