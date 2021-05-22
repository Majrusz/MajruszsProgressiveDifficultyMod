package com.majruszs_difficulty.goals;

import com.majruszs_difficulty.entities.CreeperlingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.EnumSet;

/** Makes the creeper explode if there is a player behind the wall. */
public class CreeperExplodeWallsGoal extends Goal {
	private final CreeperEntity creeper;
	private final double maximumStartDistance = 19.0;
	private final double maximumExplodeDistance = 49.0;
	private final double offset = 10.0;
	private LivingEntity attackTarget;

	public CreeperExplodeWallsGoal( CreeperEntity creeper ) {
		this.creeper = creeper;
		this.setMutexFlags( EnumSet.of( Goal.Flag.MOVE ) );
	}

	/** Returns whether execution should begin. */
	public boolean shouldExecute() {
		LivingEntity target = getNearestPlayer( this.creeper );

		return this.creeper.getCreeperState() > 0 || target != null && this.creeper.getDistanceSq( target ) < this.maximumStartDistance * getDistanceMultiplier();
	}

	/** Executes task at the beginning of goal. */
	public void startExecuting() {
		this.creeper.getNavigator()
			.clearPath();
		this.attackTarget = getNearestPlayer( this.creeper );
	}

	/** Resets the task's internal state. */
	public void resetTask() {
		this.attackTarget = null;
	}

	/** Updates state of goal each tick. */
	public void tick() {
		if( this.attackTarget == null || this.creeper.getDistanceSq( this.attackTarget ) > this.maximumExplodeDistance * getDistanceMultiplier() ) {
			this.creeper.setCreeperState( -1 ); // stops creeper's explosion
		} else {
			this.creeper.setCreeperState( 1 );
		}
	}

	/** Returns distance multiplier depending on entity is Creeper or Creeperling. */
	private double getDistanceMultiplier() {
		return this.creeper instanceof CreeperlingEntity ? 0.6 : 1.0;
	}
	/** Returns nearest player. */
	@Nullable
	private PlayerEntity getNearestPlayer( CreeperEntity creeper ) {
		if( !( creeper.world instanceof ServerWorld ) )
			return null;

		ServerWorld world = ( ServerWorld )creeper.world;
		double x = creeper.getPosX(), y = creeper.getPosY(), z = creeper.getPosZ();
		AxisAlignedBB axisAlignedBB = new AxisAlignedBB( x - this.offset, y - this.offset, z - this.offset, x + this.offset, y + this.offset,
			z + this.offset
		);

		PlayerEntity nearestPlayer = null;
		for( PlayerEntity player : world.getEntitiesWithinAABB( PlayerEntity.class, axisAlignedBB ) )
			if( !player.abilities.isCreativeMode && ( nearestPlayer == null || creeper.getDistanceSq( player ) < creeper.getDistanceSq(
				nearestPlayer )
			) )
				nearestPlayer = player;

		return nearestPlayer;
	}
}

