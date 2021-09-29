package com.majruszs_difficulty.goals;

import com.majruszs_difficulty.entities.TankEntity;
import com.mlib.CommonHelper;
import com.mlib.Random;
import com.mlib.entities.EntityHelper;
import com.mlib.math.VectorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class TankAttackGoal extends MeleeAttackGoal {
	private final TankEntity tank;
	private boolean hasAttacked = false;

	public TankAttackGoal( TankEntity tank ) {
		super( tank, 1.0, true );
		this.tank = tank;
	}

	public void tick() {
		super.tick();
		this.tank.setAggressive( !this.tank.isAttacking() && getTicksUntilNextAttack() < getAttackInterval() / 2 );
	}

	@Override
	protected void checkAndPerformAttack( LivingEntity entity, double distance ) {
		Vec3 tankPosition = new Vec3( this.mob.getX(), this.mob.getY( 0.5 ), this.mob.getZ() );
		distance = VectorHelper.distance( tankPosition, entity.position() );

		if( distance <= getAttackReachSqr( entity ) && isTimeToAttack() && !this.tank.isAttacking() ) {
			this.tank.useAttack( Random.tryChance( 0.25 ) ? TankEntity.AttackType.SPECIAL : TankEntity.AttackType.NORMAL );
		} else if( !this.hasAttacked ) {
			if( this.tank.isAttacking( TankEntity.AttackType.NORMAL ) && this.tank.getAttackDurationRatioLeft() > 0.45f ) {
				if( distance <= getAttackReachSqr( entity ) ) {
					this.tank.doHurtTarget( entity );
					this.tank.playSound( SoundEvents.SKELETON_HURT, 0.75f,  0.9f );
				}
			} else if( this.tank.isAttacking( TankEntity.AttackType.SPECIAL ) && this.tank.getAttackDurationRatioLeft() > 0.55f ) {
				hurtAllEntitiesInRange( entity );
				this.tank.playSound( SoundEvents.SKELETON_HURT, 0.75f,  0.9f );
			} else {
				return;
			}

			this.hasAttacked = true;
		} else if( this.tank.isAttackLastTick() ) {
			this.resetAttackCooldown();
			this.hasAttacked = false;
		}
	}

	@Override
	protected double getAttackReachSqr( LivingEntity entity ) {
		return super.getAttackReachSqr( entity ) * 0.6;
	}

	protected void hurtAllEntitiesInRange( LivingEntity target ) {
		ServerLevel level = CommonHelper.castIfPossible( ServerLevel.class, this.tank.level );
		if( level == null )
			return;

		Vec3 position = getSpecialAttackPosition( this.tank.position(), target.position() );
		Predicate< LivingEntity > notTankPredicate = entity->!entity.equals( this.tank );
		List< LivingEntity > entities = EntityHelper.getEntitiesInSphere( LivingEntity.class, level, position, 7.0, notTankPredicate );
		for( LivingEntity entity : entities )
			this.tank.doHurtTarget( entity );

		spawnSpecialAttackEffects( level, position );
	}

	protected Vec3 getSpecialAttackPosition( Vec3 tankPosition, Vec3 targetPosition ) {
		Vec3 normalizedOffset = VectorHelper.normalize( VectorHelper.subtract( targetPosition, tankPosition ) );

		return VectorHelper.add( tankPosition, VectorHelper.multiply( normalizedOffset, 1.75 ) );
	}

	protected void spawnSpecialAttackEffects( ServerLevel level, Vec3 position ) {
		Optional< BlockState > blockState = Optional.ofNullable( getBlockStateBelowPosition( level, position ) );
		BlockParticleOption blockParticleOption = new BlockParticleOption( ParticleTypes.BLOCK, blockState.orElse( Blocks.DIRT.defaultBlockState() ) )
			.setPos( new BlockPos( position ) );

		level.sendParticles( blockParticleOption, position.x, position.y + 0.25, position.z, 120, 1.0, 0.25, 1.0, 0.5 );
	}

	@Nullable
	protected BlockState getBlockStateBelowPosition( ServerLevel level, Vec3 position ) {
		int y = ( int )position.y;
		while( y > 0 ) {
			BlockState blockState = level.getBlockState( new BlockPos( position.x, y, position.z ) );
			if( !blockState.isAir() )
				return blockState;

			--y;
		}

		return null;
	}
}