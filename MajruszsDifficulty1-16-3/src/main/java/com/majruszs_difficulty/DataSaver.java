package com.majruszs_difficulty;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.world.WorldEvent;

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

	public static void onLoadingWorld( WorldEvent.Load event ) {
		if( !( event.getWorld() instanceof ServerWorld ) )
			return;

		ServerWorld world = ( ServerWorld )event.getWorld();

		if( world.isRemote )
			return;

		DataSaver saver = DataSaver.getDataFor( world );

		if( saver.data.contains( "DifficultyState" ) ) {
			GameState.changeMode( GameState.convertIntegerToMode( saver.data.getInt( "DifficultyState" ) ) );
		}
	}

	public static void onSavingWorld( WorldEvent.Save event ) {
		if( !( event.getWorld() instanceof ServerWorld ) )
			return;

		ServerWorld world = ( ServerWorld )event.getWorld();

		if( world.isRemote )
			return;

		DataSaver saver = DataSaver.getDataFor( world );
		CompoundNBT data = new CompoundNBT();
		data.putInt( "DifficultyState", GameState.convertModeToInteger( GameState.getCurrentMode() ) );
		saver.data = data;
		saver.markDirty();
	}
}
