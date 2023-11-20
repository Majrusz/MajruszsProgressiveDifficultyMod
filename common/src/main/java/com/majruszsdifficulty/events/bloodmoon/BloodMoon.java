package com.majruszsdifficulty.events.bloodmoon;

import com.majruszlibrary.contexts.base.Contexts;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszsdifficulty.events.bloodmoon.contexts.OnBloodMoonFinished;
import com.majruszsdifficulty.events.bloodmoon.contexts.OnBloodMoonStarted;

public class BloodMoon {
	boolean isActive = false;

	static {
		Serializables.get( BloodMoon.class )
			.define( "is_active", Reader.bool(), s->s.isActive, ( s, v )->s.isActive = v );
	}

	public boolean start() {
		if( !this.isActive && !Contexts.dispatch( new OnBloodMoonStarted() ).isCancelled() ) {
			this.isActive = true;

			return true;
		}

		return false;
	}

	public boolean finish() {
		if( this.isActive ) {
			Contexts.dispatch( new OnBloodMoonFinished() );
			this.isActive = false;

			return true;
		}

		return false;
	}

	public boolean isActive() {
		return this.isActive;
	}
}
