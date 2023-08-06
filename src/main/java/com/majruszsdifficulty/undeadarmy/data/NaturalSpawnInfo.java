package com.majruszsdifficulty.undeadarmy.data;

import com.mlib.data.SerializableStructure;

public class NaturalSpawnInfo extends SerializableStructure {
	public boolean isNaturalSpawn;

	public NaturalSpawnInfo( boolean isNaturalSpawn ) {
		super( "UndeadArmy" );

		this.isNaturalSpawn = isNaturalSpawn;

		this.defineBoolean( "is_natural_spawn", ()->this.isNaturalSpawn, x->this.isNaturalSpawn = x );
	}

	public NaturalSpawnInfo() {
		this( false );
	}
}
