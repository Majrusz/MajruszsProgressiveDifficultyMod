package com.majruszsdifficulty.undeadarmy.events;

import com.majruszlibrary.events.base.Event;
import com.majruszlibrary.events.base.Events;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;

import java.util.function.Consumer;

public class OnUndeadArmyStarted extends UndeadArmyEvent {
	public static Event< OnUndeadArmyStarted > listen( Consumer< OnUndeadArmyStarted > consumer ) {
		return Events.get( OnUndeadArmyStarted.class ).add( consumer );
	}

	public OnUndeadArmyStarted( UndeadArmy undeadArmy ) {
		super( undeadArmy );
	}
}
