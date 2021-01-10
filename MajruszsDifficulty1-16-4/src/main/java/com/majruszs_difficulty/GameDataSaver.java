package com.majruszs_difficulty;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;

/** Saving current data like current game state. */
public class GameDataSaver extends WorldSavedData {
	public static final String DATA_NAME = MajruszsDifficulty.MOD_ID;
	private CompoundNBT data = new CompoundNBT();

	public GameDataSaver() {
		super( MajruszsDifficulty.MOD_ID );
	}

	@Override
	public void read( CompoundNBT nbt ) {
		this.data = nbt.getCompound( "MajruszsDifficultyCompound" );

		GameState.changeMode( GameState.convertIntegerToMode( this.data.getInt( "DifficultyState" ) ) );
	}

	@Override
	public CompoundNBT write( CompoundNBT nbt ) {
		this.data.putInt( "DifficultyState", GameState.convertModeToInteger( GameState.getCurrentMode() ) );

		nbt.put( "MajruszsDifficultyCompound", this.data );
		return nbt;
	}
}
