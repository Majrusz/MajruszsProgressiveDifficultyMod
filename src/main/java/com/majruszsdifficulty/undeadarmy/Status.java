package com.majruszsdifficulty.undeadarmy;

public enum Status {
	STARTED, WAVE_PREPARING, WAVE_ONGOING, UNDEAD_DEFEATED, UNDEAD_WON, STOPPED, FINISHED;

	public static Status getByName( String name ) {
		for( Status status : Status.values() ) {
			if( name.equalsIgnoreCase( status.name() ) ) {
				return status;
			}
		}

		return STARTED;
	}
}
