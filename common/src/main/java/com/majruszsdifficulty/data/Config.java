package com.majruszsdifficulty.data;

import com.majruszlibrary.data.Serializables;
import com.majruszsdifficulty.bloodmoon.BloodMoonConfig;
import com.majruszsdifficulty.undeadarmy.UndeadArmyConfig;

public class Config {
	static {
		Serializables.getStatic( Config.class )
			.define( "features", Features.class )
			.define( "blood_moon", BloodMoonConfig.class )
			.define( "undead_army", UndeadArmyConfig.class )
			.define( "mobs", Mobs.class )
			.define( "items", Items.class );

		Serializables.getStatic( Features.class );

		Serializables.getStatic( Mobs.class );

		Serializables.getStatic( Items.class );
	}

	public static class Features {}

	public static class Mobs {}

	public static class Items {}
}
