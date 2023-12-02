package com.majruszsdifficulty.undeadarmy.listeners;

import com.majruszlibrary.events.base.Priority;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyTicked;

public class ParticipantsUpdater {
	static {
		OnUndeadArmyTicked.listen( ParticipantsUpdater::update )
			.priority( Priority.HIGH );
	}

	private static void update( OnUndeadArmyTicked data ) {
		data.undeadArmy.participants.clear();
		data.undeadArmy.participants.addAll( data.getServerLevel().getPlayers( player->{
			return player.isAlive() && data.undeadArmy.isInRange( player.blockPosition() );
		} ) );
	}
}
