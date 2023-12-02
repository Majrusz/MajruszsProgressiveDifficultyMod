package com.majruszsdifficulty.bloodmoon;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.events.base.Events;
import com.majruszsdifficulty.bloodmoon.events.OnBloodMoonFinished;
import com.majruszsdifficulty.bloodmoon.events.OnBloodMoonStarted;

public class BloodMoon {
	boolean isActive = false;

	static {
		Serializables.get( BloodMoon.class )
			.define( "is_active", Reader.bool(), s->s.isActive, ( s, v )->s.isActive = v );
	}

	public boolean start() {
		if( !this.isActive && BloodMoonHelper.isValidDayTime() && !Events.dispatch( new OnBloodMoonStarted() ).isCancelled() ) {
			this.isActive = true;

			return true;
		}

		return false;
	}

	public boolean finish() {
		if( this.isActive ) {
			Events.dispatch( new OnBloodMoonFinished() );
			this.isActive = false;

			return true;
		}

		return false;
	}

	public boolean isActive() {
		return this.isActive;
	}
}
