package com.majruszsdifficulty.blocks;

import com.majruszsdifficulty.Registries;
import com.mlib.contexts.OnBlockPlaced;
import com.mlib.contexts.base.Condition;
import com.mlib.modhelper.AutoInstance;
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

public class InfernalSpongeBlock extends Block {
	public static void absorb( Level level, BlockPos spongePos ) {
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
			level.setBlock( spongePos, Registries.SOAKED_INFERNAL_SPONGE.get().defaultBlockState(), 2 );
		}
	}

	public InfernalSpongeBlock() {
		super( Properties.of().mapColor( MapColor.NETHER ).strength( 0.6f ).sound( SoundType.GRASS ) );
	}

	@Override
	public void neighborChanged( BlockState blockState, Level level, BlockPos blockPos, Block neighbor, BlockPos neighborPos, boolean p_56806_ ) {
		absorb( level, blockPos );

		super.neighborChanged( blockState, level, blockPos, neighbor, neighborPos, p_56806_ );
	}

	public static class Item extends BlockItem {
		public Item() {
			super( Registries.INFERNAL_SPONGE.get(), new Properties().fireResistant() );
		}
	}

	@AutoInstance
	public static class LavaAbsorber {
		public LavaAbsorber() {
			OnBlockPlaced.listen( this::absorb )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.predicate( data->data.getPlacedBlock().is( Registries.INFERNAL_SPONGE.get() ) ) );
		}

		private void absorb( OnBlockPlaced.Data data ) {
			InfernalSpongeBlock.absorb( data.getServerLevel(), data.getBlockSnapshots().get( 0 ).getPos() );
		}
	}
}
