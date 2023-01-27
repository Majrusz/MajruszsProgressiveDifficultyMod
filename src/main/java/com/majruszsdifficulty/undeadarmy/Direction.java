package com.majruszsdifficulty.undeadarmy;

import com.mlib.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.Heightmap;

public enum Direction {
	WEST( -1, 0 ),
	EAST( 1, 0 ),
	NORTH( 0, -1 ),
	SOUTH( 0, 1 );

	final int x, z;

	Direction( int x, int z ) {
		this.x = x;
		this.z = z;
	}
}
