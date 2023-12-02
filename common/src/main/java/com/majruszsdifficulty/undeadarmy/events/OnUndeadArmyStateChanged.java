package com.majruszsdifficulty.undeadarmy.events;

import com.majruszlibrary.events.base.Event;
import com.majruszlibrary.events.base.Events;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;

import java.util.function.Consumer;

public class OnUndeadArmyStateChanged extends UndeadArmyEvent {
	public static Event< OnUndeadArmyStateChanged > listen( Consumer< OnUndeadArmyStateChanged > consumer ) {
		return Events.get( OnUndeadArmyStateChanged.class ).add( consumer );
	}

	public OnUndeadArmyStateChanged( UndeadArmy undeadArmy ) {
		super( undeadArmy );
	}
}
