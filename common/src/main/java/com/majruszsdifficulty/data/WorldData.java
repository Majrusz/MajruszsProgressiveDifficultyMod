package com.majruszsdifficulty.data;

import com.majruszlibrary.data.Serializables;

public class WorldData {
	static {
		Serializables.getStatic( WorldData.class );

		Serializables.getStatic( WorldData.Client.class );
	}

	public static class Client {}
}
