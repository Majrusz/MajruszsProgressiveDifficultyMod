package com.majruszsdifficulty.bloodmoon.events;

import com.majruszlibrary.events.base.Event;
import com.majruszlibrary.events.base.Events;

import java.util.function.Consumer;

public class OnBloodMoonFinished {
	public static Event< OnBloodMoonFinished > listen( Consumer< OnBloodMoonFinished > consumer ) {
		return Events.get( OnBloodMoonFinished.class ).add( consumer );
	}

	public OnBloodMoonFinished() {}
}
