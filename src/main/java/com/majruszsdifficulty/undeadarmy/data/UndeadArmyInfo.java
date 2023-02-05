package com.majruszsdifficulty.undeadarmy.data;

import com.mlib.data.SerializableStructure;

public class UndeadArmyInfo extends SerializableStructure {
	public Data data = new Data();

	public UndeadArmyInfo() {
		this.define( "UndeadArmy", ()->this.data, x->this.data = x, Data::new );
	}

	public static class Data extends SerializableStructure {
		public int killedUndead = 0;

		public Data() {
			this.define( "killed_undead", ()->this.killedUndead, x->this.killedUndead = x );
		}
	}
}
