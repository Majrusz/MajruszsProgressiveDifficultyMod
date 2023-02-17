package com.majruszsdifficulty.undeadarmy.data;

import com.mlib.data.SerializableStructure;

public class UndeadArmyInfo extends SerializableStructure {
	public int killedUndead = 0;

	public UndeadArmyInfo() {
		super( "UndeadArmy" );

		this.define( "killed_undead", ()->this.killedUndead, x->this.killedUndead = x );
	}
}
