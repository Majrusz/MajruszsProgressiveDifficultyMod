package com.majruszsdifficulty.undeadarmy.events;

import com.majruszlibrary.events.base.Event;
import com.majruszlibrary.events.base.Events;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;

import java.util.function.Consumer;

public class OnUndeadArmyTicked extends UndeadArmyEvent {
	public static Event< OnUndeadArmyTicked > listen( Consumer< OnUndeadArmyTicked > consumer ) {
		return Events.get( OnUndeadArmyTicked.class ).add( consumer );
	}

	public OnUndeadArmyTicked( UndeadArmy undeadArmy ) {
		super( undeadArmy );
	}
}
