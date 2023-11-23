package com.majruszsdifficulty.undeadarmy.events;

import com.majruszlibrary.events.base.Event;
import com.majruszlibrary.events.base.Events;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;

import java.util.function.Consumer;

public class OnUndeadArmyWaveFinished extends UndeadArmyEvent {
	public static Event< OnUndeadArmyWaveFinished > listen( Consumer< OnUndeadArmyWaveFinished > consumer ) {
		return Events.get( OnUndeadArmyWaveFinished.class ).add( consumer );
	}

	public OnUndeadArmyWaveFinished( UndeadArmy undeadArmy ) {
		super( undeadArmy );
	}
}
