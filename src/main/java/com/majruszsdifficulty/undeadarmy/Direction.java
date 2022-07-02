package com.majruszsdifficulty.undeadarmy;

import com.mlib.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

/** Possible directions where Undead Army can spawn. */
public enum Direction {
	WEST( -1, 0 ), EAST( 1, 0 ), NORTH( 0, -1 ), SOUTH( 0, 1 );

	private static final int DISTANCE_MULTIPLIER = 10;
	private final int xFactor, zFactor;
	public final int x, z;

	Direction( int x, int z ) {
		this.x = x;
		this.z = z;
		this.xFactor = ( this.z != 0 ? 5 : 1 ) * DISTANCE_MULTIPLIER;
		this.zFactor = ( this.x != 0 ? 5 : 1 ) * DISTANCE_MULTIPLIER;
	}

	public static Direction getRandom() {
		return Direction.values()[ Random.nextInt( 0, Direction.values().length ) ];
	}

	public static Direction getByName( String name ) {
		for( Direction direction : Direction.values() )
			if( name.equalsIgnoreCase( direction.name() ) )
				return direction;

		return WEST;
	}

	public BlockPos getRandomSpawnPosition( ServerLevel world, BlockPos positionToAttack, int spawnRadius ) {
		int tries = 0;
		int x, y, z;
		do {
			Vec3 offset = Random.getRandomVector3d( -this.xFactor, this.xFactor, 0.0, 0.0, -this.zFactor, this.zFactor );
			x = positionToAttack.getX() + this.x * spawnRadius + ( int )offset.x;
			z = positionToAttack.getZ() + this.z * spawnRadius + ( int )offset.z;
			y = world.getHeight( Heightmap.Types.MOTION_BLOCKING, x, z );
			++tries;
		} while( y != world.getHeight( Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z ) && tries < 5 );

		return new BlockPos( x, y + 1, z );
	}
}
