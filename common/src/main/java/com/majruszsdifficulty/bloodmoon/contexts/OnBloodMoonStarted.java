package com.majruszsdifficulty.bloodmoon.contexts;

import com.majruszlibrary.events.base.Event;
import com.majruszlibrary.events.base.Events;
import com.majruszlibrary.events.type.ICancellableEvent;

import java.util.function.Consumer;

public class OnBloodMoonStarted implements ICancellableEvent {
	private boolean isCancelled = false;

	public static Event< OnBloodMoonStarted > listen( Consumer< OnBloodMoonStarted > consumer ) {
		return Events.get( OnBloodMoonStarted.class ).add( consumer );
	}

	public OnBloodMoonStarted() {}

	@Override
	public boolean isExecutionStopped() {
		return this.isCancelled();
	}

	public void cancel() {
		this.isCancelled = true;
	}

	public boolean isCancelled() {
		return this.isCancelled;
	}
}
