package com.majruszsdifficulty.undeadarmy.events;

import com.majruszlibrary.events.base.Event;
import com.majruszlibrary.events.base.Events;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;

import java.util.function.Consumer;

public class OnUndeadArmyLoaded extends UndeadArmyEvent {
	public static Event< OnUndeadArmyLoaded > listen( Consumer< OnUndeadArmyLoaded > consumer ) {
		return Events.get( OnUndeadArmyLoaded.class ).add( consumer );
	}

	public OnUndeadArmyLoaded( UndeadArmy undeadArmy ) {
		super( undeadArmy );
	}
}
