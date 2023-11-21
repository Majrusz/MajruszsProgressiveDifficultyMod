package com.majruszsdifficulty.data;

import com.majruszlibrary.data.Serializables;

public class Config {
	static {
		Serializables.getStatic( Config.class )
			.define( "features", Features.class )
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
