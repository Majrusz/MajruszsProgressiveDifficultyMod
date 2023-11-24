package com.majruszsdifficulty.undeadarmy.listeners;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyDefeated;

public class AdvancementsController {
	static {
		OnUndeadArmyDefeated.listen( AdvancementsController::trigger );
	}

	private static void trigger( OnUndeadArmyDefeated data ) {
		data.undeadArmy.participants.forEach( participant->MajruszsDifficulty.HELPER.triggerAchievement( participant, "army_defeated" ) );
	}
}
