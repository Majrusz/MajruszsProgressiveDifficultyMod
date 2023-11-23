package com.majruszsdifficulty.undeadarmy.listeners;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyWaveFinished;

public class AdvancementsController {
	static {
		OnUndeadArmyWaveFinished.listen( AdvancementsController::trigger )
			.addCondition( data->data.undeadArmy.isLastWave() );
	}

	private static void trigger( OnUndeadArmyWaveFinished data ) {
		data.undeadArmy.participants.forEach( participant->MajruszsDifficulty.HELPER.triggerAchievement( participant, "army_defeated" ) );
	}
}
