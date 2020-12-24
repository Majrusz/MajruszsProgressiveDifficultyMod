package com.majruszs_difficulty;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import java.util.function.Supplier;

public class DataSaver extends WorldSavedData implements Supplier {
	public CompoundNBT data = new CompoundNBT();

	public DataSaver() {
		super( MajruszsDifficulty.MOD_ID );
	}

	@Override
	public void read( CompoundNBT nbt ) {
		this.data = nbt.getCompound( "MajruszsDifficultyCompound" );
	}

	@Override
	public CompoundNBT write( CompoundNBT nbt ) {
		nbt.put( "MajruszsDifficultyCompound", this.data );
		return nbt;
	}

	public static DataSaver getDataFor( ServerWorld world ) {
		return ( DataSaver )world.getSavedData()
			.getOrCreate( new DataSaver(), MajruszsDifficulty.MOD_ID );
	}

	@Override
	public Object get() {
		return this;
	}
}
