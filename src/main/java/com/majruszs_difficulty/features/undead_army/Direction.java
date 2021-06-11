package com.majruszs_difficulty.events.undead_army;

import com.mlib.MajruszLibrary;
import com.mlib.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

/** Possible directions where can Undead Army spawn. */
public enum Direction {
	WEST( -1, 0 ), EAST( 1, 0 ), NORTH( 0, -1 ), SOUTH( 0, 1 );

	private static final int DISTANCE_MULTIPLIER = 10;
	public final int x, z;
	private final int xFactor, zFactor;

	Direction( int x, int z ) {
		this.x = x;
		this.z = z;
		this.xFactor = ( this.z != 0 ? 5 : 1 ) * DISTANCE_MULTIPLIER;
		this.zFactor = ( this.x != 0 ? 5 : 1 ) * DISTANCE_MULTIPLIER;
	}

	/** Returns random direction. */
	public static Direction getRandom() {
		return Direction.values()[ MajruszLibrary.RANDOM.nextInt( Direction.values().length ) ];
	}

	/** Returns direction by given string. */
	public static Direction getByName( String name ) {
		for( Direction direction : Direction.values() )
			if( name.equalsIgnoreCase( direction.name() ) )
				return direction;

		return WEST;
	}

	/** Returns random position for entity to spawn in given direction. */
	public BlockPos getRandomSpawnPosition( ServerWorld world, BlockPos positionToAttack, int spawnRadius ) {
		Vector3d offset = Random.getRandomVector3d( -this.xFactor, this.xFactor, 0.0, 0.0, -this.zFactor, this.zFactor );

		int x = positionToAttack.getX() + this.x * spawnRadius + ( int )offset.x;
		int z = positionToAttack.getZ() + this.z * spawnRadius + ( int )offset.z;
		int y = world.getHeight( Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z ) + 1;

		return new BlockPos( x, y, z );
	}
}
