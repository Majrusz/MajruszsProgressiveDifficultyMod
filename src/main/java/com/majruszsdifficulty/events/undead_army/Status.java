package com.majruszsdifficulty.events.undead_army;

/** Possible Undead Army wave statuses. */
public enum Status {
	BETWEEN_WAVES, ONGOING, VICTORY, FAILED, STOPPED;

	public static Status getByName( String name ) {
		for( Status status : Status.values() )
			if( name.equalsIgnoreCase( status.name() ) )
				return status;

		return ONGOING;
	}
}
