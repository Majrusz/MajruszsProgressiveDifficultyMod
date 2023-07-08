package com.majruszsdifficulty;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.handlers.GameStageIncreaser;
import com.majruszsdifficulty.treasurebags.TreasureBagProgressManager;
import com.majruszsdifficulty.undeadarmy.Config;
import com.majruszsdifficulty.undeadarmy.UndeadArmyManager;
import com.mlib.data.SerializableHelper;
import com.mlib.data.SerializableStructure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class GameDataSaver extends SavedData {
	final Data data;

	public GameDataSaver( ServerLevel overworld ) {
		this.data = new Data( overworld );

		GameStage.changeStage( GameStageIncreaser.getDefaultGameStage(), null );
	}

	public GameDataSaver( ServerLevel overworld, CompoundTag tag ) {
		this.data = new Data( overworld );

		this.data.read( tag );
	}

	@Override
	public CompoundTag save( CompoundTag tag ) {
		return SerializableHelper.write( ()->this.data, tag );
	}

	public UndeadArmyManager getUndeadArmyManager() {
		return this.data.undeadArmyManager;
	}

	public TreasureBagProgressManager getTreasureBagProgressManager() {
		return this.data.treasureBagProgressManager;
	}

	public static class Data extends SerializableStructure {
		UndeadArmyManager undeadArmyManager;
		TreasureBagProgressManager treasureBagProgressManager;

		public Data( ServerLevel overworld ) {
			super( "MajruszsDifficulty" );

			Config config = Registries.HELPER.findInstance( Config.class ).orElseThrow();
			this.undeadArmyManager = new UndeadArmyManager( overworld, config );
			this.treasureBagProgressManager = new TreasureBagProgressManager();

			this.defineEnum( "GameStage", GameStage::getCurrentStage, gameStage->GameStage.changeStage( gameStage, null ), GameStage::values );
			this.defineCustom( "UndeadArmy", ()->this.undeadArmyManager, x->this.undeadArmyManager = x, ()->new UndeadArmyManager( overworld, config ) );
			this.defineCustom( "TreasureBags", ()->this.treasureBagProgressManager, x->this.treasureBagProgressManager = x, TreasureBagProgressManager::new );
		}
	}
}
