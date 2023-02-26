package com.majruszsdifficulty;

import com.majruszsdifficulty.gamemodifiers.list.IncreaseGameStage;
import com.majruszsdifficulty.treasurebags.TreasureBagProgressManager;
import com.majruszsdifficulty.undeadarmy.Config;
import com.majruszsdifficulty.undeadarmy.UndeadArmyManager;
import com.mlib.data.SerializableStructure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class GameDataSaver extends SavedData {
	final Data data;

	public GameDataSaver( ServerLevel overworld ) {
		this.data = new Data( overworld );

		GameStage.changeStage( IncreaseGameStage.getDefaultGameStage(), null );
	}

	public GameDataSaver( ServerLevel overworld, CompoundTag tag ) {
		this.data = new Data( overworld );

		this.data.read( tag );
	}

	@Override
	public CompoundTag save( CompoundTag tag ) {
		this.data.write( tag );

		return tag;
	}

	public UndeadArmyManager getUndeadArmyManager() {
		return this.data.undeadArmyManager;
	}

	public TreasureBagProgressManager getTreasureBagProgressManager() {
		return this.data.treasureBagProgressManager;
	}

	public static class Data extends SerializableStructure {
		final UndeadArmyManager undeadArmyManager;
		final TreasureBagProgressManager treasureBagProgressManager;

		public Data( ServerLevel overworld ) {
			super( "MajruszsDifficulty" );

			this.undeadArmyManager = new UndeadArmyManager( overworld, Registries.ANNOTATION_HANDLER.getInstance( Config.class ) );
			this.treasureBagProgressManager = new TreasureBagProgressManager();

			this.define( "GameStage", GameStage::getCurrentStage, gameStage->GameStage.changeStage( gameStage, null ), GameStage::values );
			this.define( "UndeadArmy", ()->this.undeadArmyManager );
			this.define( "TreasureBags", ()->this.treasureBagProgressManager );
		}
	}
}
