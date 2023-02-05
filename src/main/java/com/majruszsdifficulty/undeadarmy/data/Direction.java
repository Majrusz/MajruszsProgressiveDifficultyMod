package com.majruszsdifficulty.undeadarmy.data;

public enum Direction {
	WEST( -1, 0 ),
	EAST( 1, 0 ),
	NORTH( 0, -1 ),
	SOUTH( 0, 1 );

	public final int x, z;

	Direction( int x, int z ) {
		this.x = x;
		this.z = z;
	}
}
