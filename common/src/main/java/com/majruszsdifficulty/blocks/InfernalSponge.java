package com.majruszsdifficulty.blocks;

import com.majruszlibrary.events.OnBlockPlaced;
import com.majruszlibrary.events.base.Condition;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;

public class InfernalSponge extends Block {
	static {
		OnBlockPlaced.listen( InfernalSponge::absorb )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->data.blockState.is( MajruszsDifficulty.Blocks.INFERNAL_SPONGE.get() ) );
	}

	public InfernalSponge() {
		super( Properties.of().mapColor( MapColor.NETHER ).strength( 0.6f ).sound( SoundType.GRASS ) );
	}

	@Override
	public void neighborChanged( BlockState blockState, Level level, BlockPos blockPos, Block neighbor, BlockPos neighborPos, boolean p_56806_ ) {
		InfernalSponge.absorb( level, blockPos );

		super.neighborChanged( blockState, level, blockPos, neighbor, neighborPos, p_56806_ );
	}

	private static void absorb( OnBlockPlaced data ) {
		InfernalSponge.absorb( data.getServerLevel(), data.position );
	}

	private static void absorb( Level level, BlockPos spongePos ) {
		int absorbedBlocks = BlockPos.breadthFirstTraversal( spongePos, 6, 65, ( blockPos, consumer )->{
			for( Direction direction : Direction.values() ) {
				consumer.accept( blockPos.relative( direction ) );
			}
		}, blockPos->{
			if( blockPos.equals( spongePos ) ) {
				return true;
			}

			BlockState blockState = level.getBlockState( blockPos );
			FluidState fluidState = level.getFluidState( blockPos );
			if( !fluidState.is( Fluids.LAVA ) && !fluidState.is( Fluids.FLOWING_LAVA ) ) {
				return false;
			}
			if( blockState.getBlock() instanceof BucketPickup bucketPickup && !bucketPickup.pickupBlock( level, blockPos, blockState ).isEmpty() ) {
				return true;
			}

			if( blockState.getBlock() instanceof LiquidBlock ) {
				level.setBlock( blockPos, Blocks.AIR.defaultBlockState(), 3 );
			} else {
				BlockEntity blockentity = blockState.hasBlockEntity() ? level.getBlockEntity( blockPos ) : null;
				dropResources( blockState, level, blockPos, blockentity );
				level.setBlock( blockPos, Blocks.AIR.defaultBlockState(), 3 );
			}

			return true;
		} ) - 1;

		if( absorbedBlocks > 0 ) {
			level.setBlock( spongePos, MajruszsDifficulty.Blocks.SOAKED_INFERNAL_SPONGE.get().defaultBlockState(), 2 );
		}
	}

	public static class Item extends BlockItem {
		public Item() {
			super( MajruszsDifficulty.Blocks.INFERNAL_SPONGE.get(), new Properties().fireResistant() );
		}
	}
}
