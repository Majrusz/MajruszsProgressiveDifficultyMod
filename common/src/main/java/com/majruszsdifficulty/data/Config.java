package com.majruszsdifficulty.data;

import com.majruszlibrary.data.Serializables;

public class Config {
	static {
		Serializables.getStatic( Config.class )
			.define( "features", Features.class )
			.define( "mobs", Mobs.class );

		Serializables.getStatic( Features.class );

		Serializables.getStatic( Mobs.class );
	}

	public static class Features {}

	public static class Mobs {}
}
