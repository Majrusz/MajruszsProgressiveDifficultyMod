package com.majruszsdifficulty.undeadarmy.events;

import com.majruszlibrary.events.base.Event;
import com.majruszlibrary.events.base.Events;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;

import java.util.function.Consumer;

public class OnUndeadArmyDefeated extends UndeadArmyEvent {
	public static Event< OnUndeadArmyDefeated > listen( Consumer< OnUndeadArmyDefeated > consumer ) {
		return Events.get( OnUndeadArmyDefeated.class ).add( consumer );
	}

	public OnUndeadArmyDefeated( UndeadArmy undeadArmy ) {
		super( undeadArmy );
	}
}
