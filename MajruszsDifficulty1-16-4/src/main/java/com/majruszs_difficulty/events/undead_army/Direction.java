package com.majruszs_difficulty.events.undead_army;

import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

public enum Direction {
	WEST( -1, 0 ), EAST( 1, 0 ), NORTH( 0, -1 ), SOUTH( 0, 1 );

	private static final int standardDeviation = 10;
	public final int x, z;

	Direction( int x, int z ) {
		this.x = x;
		this.z = z;
	}

	public BlockPos getRandomSpawnPosition( ServerWorld world, BlockPos positionToAttack, int spawnRadius ) {
		int xFactor = ( this.z != 0 ? 5 : 1 ) * standardDeviation;
		int zFactor = ( this.x != 0 ? 5 : 1 ) * standardDeviation;

		int x = positionToAttack.getX() + this.x * spawnRadius + MajruszsDifficulty.RANDOM.nextInt( xFactor * 2 ) - xFactor;
		int z = positionToAttack.getZ() + this.z * spawnRadius + MajruszsDifficulty.RANDOM.nextInt( zFactor * 2 ) - zFactor;
		int y = world.getHeight( Heightmap.Type.WORLD_SURFACE, x, z );

		return new BlockPos( x, y, z );
	}

	public static Direction getRandom() {
		return Direction.values()[ MajruszsDifficulty.RANDOM.nextInt( Direction.values().length ) ];
	}

	public static Direction getByName( String name ) {
		for( Direction direction : Direction.values() )
			if( name.equalsIgnoreCase( direction.name() ) )
				return direction;

		return WEST;
	}
}
