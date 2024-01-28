package com.majruszsdifficulty.blocks;

import com.google.common.collect.Lists;
import com.majruszlibrary.events.OnBlockPlaced;
import com.majruszlibrary.events.base.Condition;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.Queue;

public class InfernalSponge extends Block {
	static {
		OnBlockPlaced.listen( InfernalSponge::absorb )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->data.blockState.is( MajruszsDifficulty.INFERNAL_SPONGE_BLOCK.get() ) );
	}

	public InfernalSponge() {
		super( Properties.of( Material.SPONGE, MaterialColor.NETHER ).strength( 0.6f ).sound( SoundType.GRASS ) );
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
		Queue< Tuple<BlockPos, Integer> > $$2 = Lists.newLinkedList();
		$$2.add(new Tuple(spongePos, 0));
		int absorbedBlocks = 0;

		while(!$$2.isEmpty()) {
			Tuple<BlockPos, Integer> $$4 = (Tuple)$$2.poll();
			BlockPos $$5 = (BlockPos)$$4.getA();
			int $$6 = (Integer)$$4.getB();
			Direction[] var8 = Direction.values();
			int var9 = var8.length;

			for(int var10 = 0; var10 < var9; ++var10) {
				Direction $$7 = var8[var10];
				BlockPos $$8 = $$5.relative($$7);
				BlockState $$9 = level.getBlockState($$8);
				FluidState spongePos0 = level.getFluidState($$8);
				if (spongePos0.is( FluidTags.LAVA)) {
					if ($$9.getBlock() instanceof BucketPickup && !((BucketPickup)$$9.getBlock()).pickupBlock(level, $$8, $$9).isEmpty()) {
						++absorbedBlocks;
						if ($$6 < 6) {
							$$2.add(new Tuple($$8, $$6 + 1));
						}
					} else if ($$9.getBlock() instanceof LiquidBlock) {
						level.setBlock($$8, Blocks.AIR.defaultBlockState(), 3);
						++absorbedBlocks;
						if ($$6 < 6) {
							$$2.add(new Tuple($$8, $$6 + 1));
						}
					}
				}
			}

			if (absorbedBlocks > 64) {
				break;
			}
		}

		if( absorbedBlocks > 0 ) {
			level.setBlock( spongePos, MajruszsDifficulty.SOAKED_INFERNAL_SPONGE_BLOCK.get().defaultBlockState(), 2 );
		}
	}

	public static class Item extends BlockItem {
		public Item() {
			super( MajruszsDifficulty.INFERNAL_SPONGE_BLOCK.get(), new Properties().rarity( Rarity.UNCOMMON ).fireResistant() );
		}
	}
}
