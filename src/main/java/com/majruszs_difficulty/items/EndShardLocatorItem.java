package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/** Item for locating End Shards. */
public class EndShardLocatorItem extends Item {
	private static final String COUNTER_TAG = "EndShardLocatorCounter";
	private static final String POSITION_X_TAG = "EndShardLocatorX";
	private static final String POSITION_Y_TAG = "EndShardLocatorY";
	private static final String POSITION_Z_TAG = "EndShardLocatorZ";
	private static final int COORDINATE_FACTOR = 30;
	private static final float INVALID_DISTANCE = 9001.0f;

	public EndShardLocatorItem() {
		super( ( new Item.Properties() ).group( Instances.ITEM_GROUP )
			.rarity( Rarity.UNCOMMON )
			.maxStackSize( 1 ) );
	}

	/** Calculates distance to the nearest End Shard. */
	public static float calculateDistanceToEndShard( ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity entity ) {
		if( entity == null )
			return INVALID_DISTANCE;

		World world = entity.world;
		BlockPos entityPosition = entity.getPosition();

		CompoundNBT data = entity.getPersistentData();
		int counter = data.getInt( COUNTER_TAG );
		BlockPos nearestEndShard = new BlockPos( data.getInt( POSITION_X_TAG ), data.getInt( POSITION_Y_TAG ), data.getInt( POSITION_Z_TAG ) );
		BlockState currentBlockState = world.getBlockState( nearestEndShard );
		boolean isValid = currentBlockState.getBlock() == Instances.END_SHARD_ORE;

		double closestDistance = entityPosition.distanceSq( nearestEndShard );
		BlockPos endShardOre = findNearestEndShard( world, entityPosition, counter - COORDINATE_FACTOR );
		if( endShardOre != null ) {
			if( !isValid ) {
				nearestEndShard = endShardOre;
			} else {
				double currentDistance = entityPosition.distanceSq( endShardOre );
				if( currentDistance < closestDistance )
					nearestEndShard = endShardOre;
			}
		}

		data.putInt( COUNTER_TAG, ( counter + 1 ) % ( 2 * COORDINATE_FACTOR ) );
		data.putInt( POSITION_X_TAG, nearestEndShard.getX() );
		data.putInt( POSITION_Y_TAG, nearestEndShard.getY() );
		data.putInt( POSITION_Z_TAG, nearestEndShard.getZ() );

		return isValid ? ( float )closestDistance : INVALID_DISTANCE;
	}

	/**
	 Finds nearest End Shard Ore in certain distance. This function looks for End Shard Ore in this area:
	 x -> entity position +- factor
	 y -> function parameter
	 z -> entity position +- factor
	 */
	@Nullable
	private static BlockPos findNearestEndShard( World world, BlockPos entityPosition, int yFactor ) {
		int y = entityPosition.getY() + yFactor;
		double closestDistance = INVALID_DISTANCE;
		BlockPos blockPosition = new BlockPos( 0, 0, 0 );

		for( int x = entityPosition.getX() - COORDINATE_FACTOR; x < entityPosition.getX() + COORDINATE_FACTOR; x++ )
			for( int z = entityPosition.getZ() - COORDINATE_FACTOR; z < entityPosition.getZ() + COORDINATE_FACTOR; z++ ) {
				BlockPos testPosition = new BlockPos( x, y, z );
				BlockState testBlockState = world.getBlockState( testPosition );
				if( testBlockState.getBlock() == Instances.END_SHARD_ORE ) {
					double distance = testPosition.distanceSq( entityPosition );
					if( distance < closestDistance ) {
						closestDistance = distance;
						blockPosition = testPosition;
					}
				}
			}

		return closestDistance == INVALID_DISTANCE ? null : blockPosition;
	}
}
