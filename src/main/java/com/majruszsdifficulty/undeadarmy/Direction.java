package com.majruszsdifficulty.undeadarmy;

import com.mlib.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.Heightmap;

/** Possible directions where Undead Army can spawn. */
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

	public void write( CompoundTag nbt ) {
		nbt.putString( Direction.class.getName(), this.toString() );
	}

	public static Direction read( CompoundTag nbt ) {
		String name = nbt.getString( Direction.class.getName() );
		for( Direction direction : Direction.values() ) {
			if( name.equalsIgnoreCase( direction.name() ) ) {
				return direction;
			}
		}

		return WEST;
	}

	public static Direction getRandom() {
		return Random.nextRandom( Direction.values() );
	}

	public BlockPos getRandomSpawnPosition( ServerLevel level, BlockPos positionToAttack, int spawnRadius ) {
		int tries = 0;
		int x, y, z;
		do {
			Vec3i offset = this.buildOffset( spawnRadius );
			x = positionToAttack.getX() + offset.getX();
			z = positionToAttack.getZ() + offset.getZ();
			y = level.getHeight( Heightmap.Types.MOTION_BLOCKING, x, z );
		} while( y != level.getHeight( Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z ) && ++tries < 5 );

		return new BlockPos( x, y + 1, z );
	}

	private Vec3i buildOffset( int spawnRadius ) {
		int x = this.z != 0 ? 50 : 10 + this.x * spawnRadius;
		int y = 0;
		int z = this.x != 0 ? 50 : 10 + this.z * spawnRadius;

		return Random.getRandomVector3i( -x, x, -y, y, -z, z );
	}
}
