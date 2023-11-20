package com.majruszsdifficulty.events.bloodmoon.contexts;

import com.majruszlibrary.contexts.base.Context;
import com.majruszlibrary.contexts.base.Contexts;

import java.util.function.Consumer;

public class OnBloodMoonFinished {
	public static Context< OnBloodMoonFinished > listen( Consumer< OnBloodMoonFinished > consumer ) {
		return Contexts.get( OnBloodMoonFinished.class ).add( consumer );
	}

	public OnBloodMoonFinished() {}
}
