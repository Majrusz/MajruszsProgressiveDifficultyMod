package com.majruszsdifficulty.events.bloodmoon.contexts;

import com.mlib.contexts.base.Context;
import com.mlib.contexts.base.Contexts;
import com.mlib.contexts.data.ICancellableData;

import java.util.function.Consumer;

public class OnBloodMoonStarted implements ICancellableData {
	private boolean isCancelled = false;

	public static Context< OnBloodMoonStarted > listen( Consumer< OnBloodMoonStarted > consumer ) {
		return Contexts.get( OnBloodMoonStarted.class ).add( consumer );
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
