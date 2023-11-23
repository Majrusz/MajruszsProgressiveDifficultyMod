package com.majruszsdifficulty.undeadarmy.listeners;

import com.majruszlibrary.entity.EntityHelper;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.majruszsdifficulty.undeadarmy.UndeadArmyConfig;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyWaveFinished;

public class RewardsController {
	static {
		OnUndeadArmyWaveFinished.listen( RewardsController::update );
	}

	private static void update( OnUndeadArmyWaveFinished data ) {
		RewardsController.giveExperienceReward( data.undeadArmy );
		if( data.undeadArmy.isLastWave() ) {
			// TODO: treasure bag
			if( UndeadArmyConfig.RESET_ALL_PARTICIPANTS_REQUIREMENT ) {
				RewardsController.resetAllKillRequirements( data.undeadArmy );
			}
		}
	}

	private static void giveExperienceReward( UndeadArmy undeadArmy ) {
		UndeadArmyConfig.WaveDef waveDef = UndeadArmyConfig.WAVE_DEFS.get( undeadArmy.currentWave );
		undeadArmy.participants.forEach( participant->{
			for( int i = 0; i < waveDef.experience / 4; ++i ) {
				EntityHelper.spawnExperience( undeadArmy.getLevel(), participant.position(), 4 );
			}
		} );
	}

	private static void resetAllKillRequirements( UndeadArmy undeadArmy ) {
		undeadArmy.participants.forEach( participant->{
			// TODO: update
			// undeadArmy.config.modifyUndeadArmyInfo( participant.getPersistentData(), info->info.killedUndead = 0 );
		} );
	}
}
