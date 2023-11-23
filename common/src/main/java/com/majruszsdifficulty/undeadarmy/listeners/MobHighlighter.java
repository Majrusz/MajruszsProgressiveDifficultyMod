package com.majruszsdifficulty.undeadarmy.listeners;

import com.majruszlibrary.time.TimeHelper;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.majruszsdifficulty.undeadarmy.UndeadArmyConfig;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyTicked;

public class MobHighlighter {
	static {
		OnUndeadArmyTicked.listen( MobHighlighter::highlight )
			.addCondition( data->TimeHelper.haveSecondsPassed( 4.0 ) )
			.addCondition( data->data.undeadArmy.phase.state == UndeadArmy.Phase.State.WAVE_ONGOING )
			.addCondition( data->data.undeadArmy.phase.getTicksActive() >= TimeHelper.toTicks( UndeadArmyConfig.HIGHLIGHT_DELAY ) );
	}

	private static void highlight( OnUndeadArmyTicked data ) {
		data.undeadArmy.highlight();
	}
}
