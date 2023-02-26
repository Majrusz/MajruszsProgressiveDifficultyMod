package com.majruszsdifficulty;

import com.majruszsdifficulty.gamemodifiers.list.IncreaseGameStage;
import com.mlib.data.SerializableStructure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class GameDataSaver extends SavedData {
	final Data data = new Data();

	public GameDataSaver( CompoundTag tag ) {
		this.data.read( tag );
	}

	public GameDataSaver() {
		GameStage.changeStage( IncreaseGameStage.getDefaultGameStage(), null );
	}

	@Override
	public CompoundTag save( CompoundTag tag ) {
		this.data.write( tag );

		return tag;
	}

	public static class Data extends SerializableStructure {
		public Data() {
			super( "MajruszsDifficulty" );

			this.define( "GameStage", GameStage::getCurrentStage, gameStage->GameStage.changeStage( gameStage, null ), GameStage::values );
		}
	}
}
