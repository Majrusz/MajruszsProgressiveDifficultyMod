package com.majruszsdifficulty.undeadarmy.data;

import com.mlib.data.SerializableStructure;

public class UndeadArmyInfo extends SerializableStructure {
	public int killedUndead;

	public UndeadArmyInfo( int initialKillsCount ) {
		super( "UndeadArmy" );

		this.killedUndead = initialKillsCount;

		this.defineInteger( "killed_undead", ()->this.killedUndead, x->this.killedUndead = x );
	}
}
