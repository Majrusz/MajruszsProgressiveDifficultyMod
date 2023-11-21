package com.majruszsdifficulty.blocks;

import com.majruszlibrary.math.Random;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import java.util.List;

public class SoakedInfernalSponge extends Block {
	public SoakedInfernalSponge() {
		super( Properties.of().mapColor( MapColor.NETHER ).strength( 0.6f ).sound( SoundType.GRASS ).lightLevel( x->13 ) );
	}

	@Override
	public void animateTick( BlockState blockState, Level level, BlockPos blockPos, RandomSource random ) {
		Direction direction = Random.next( List.of( Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST ) );
		BlockPos offsetPos = blockPos.relative( direction );
		if( !blockState.canOcclude() || !level.getBlockState( offsetPos ).isFaceSturdy( level, offsetPos, direction.getOpposite() ) ) {
			double x = blockPos.getX();
			double y = blockPos.getY();
			double z = blockPos.getZ();
			if( direction == Direction.DOWN ) {
				y -= 0.05;
				x += random.nextDouble();
				z += random.nextDouble();
			} else {
				y += random.nextDouble() * 0.8;
				if( direction.getAxis() == Direction.Axis.X ) {
					z += random.nextDouble();
					if( direction == Direction.EAST ) {
						++x;
					} else {
						x += 0.05;
					}
				} else {
					x += random.nextDouble();
					if( direction == Direction.SOUTH ) {
						++z;
					} else {
						z += 0.05;
					}
				}
			}

			ParticleOptions particle = level.getBlockState( offsetPos ).is( Blocks.WATER ) ? ParticleTypes.SMOKE : ParticleTypes.DRIPPING_LAVA;
			level.addParticle( particle, x, y, z, 0.0, 0.0, 0.0 );
		}
	}

	@Override
	public void stepOn( Level level, BlockPos blockPos, BlockState blockState, Entity entity ) {
		if( !entity.isSteppingCarefully() && entity instanceof LivingEntity livingEntity && !EnchantmentHelper.hasFrostWalker( livingEntity ) ) {
			entity.hurt( level.damageSources().hotFloor(), 1.0f );
		}

		super.stepOn( level, blockPos, blockState, entity );
	}

	public static class Item extends BlockItem {
		public Item() {
			super( MajruszsDifficulty.Blocks.SOAKED_INFERNAL_SPONGE.get(), new Properties().fireResistant() );
		}
	}
}
