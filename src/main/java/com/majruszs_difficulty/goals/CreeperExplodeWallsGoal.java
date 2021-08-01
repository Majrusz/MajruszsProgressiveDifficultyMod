package com.majruszs_difficulty.goals;

import com.majruszs_difficulty.entities.CreeperlingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.EnumSet;

/** Makes the creeper explode if there is a player behind the wall. */
public class CreeperExplodeWallsGoal extends Goal {
	private final Creeper creeper;
	private final double maximumStartDistance = 19.0;
	private final double maximumExplodeDistance = 49.0;
	private final double offset = 10.0;
	private LivingEntity attackTarget;

	public CreeperExplodeWallsGoal( Creeper creeper ) {
		this.creeper = creeper;
		this.setFlags( EnumSet.of( Goal.Flag.MOVE ) );
	}

	/** Returns whether execution should begin. */
	@Override
	public boolean canUse() {
		LivingEntity target = getNearestPlayer( this.creeper );

		return this.creeper.getSwellDir() > 0 || target != null && this.creeper.distanceToSqr(
			target ) < this.maximumStartDistance * getDistanceMultiplier();
	}

	/** Updates state of goal each tick. */
	public void tick() {
		if( this.attackTarget == null || this.creeper.distanceToSqr( this.attackTarget ) > this.maximumExplodeDistance * getDistanceMultiplier() ) {
			this.creeper.setSwellDir( -1 ); // stops creeper's explosion
		} else {
			this.creeper.setSwellDir( 1 );
		}
	}

	/** Executes task at the beginning of goal. */
	public void startExecuting() {
		this.creeper.getNavigation()
			.stop();
		this.attackTarget = getNearestPlayer( this.creeper );
	}

	/** Resets the task's internal state. */
	public void resetTask() {
		this.attackTarget = null;
	}

	/** Returns distance multiplier depending on entity is Creeper or Creeperling. */
	private double getDistanceMultiplier() {
		return this.creeper instanceof CreeperlingEntity ? 0.6 : 1.0;
	}

	/** Returns nearest player. */
	@Nullable
	private Player getNearestPlayer( Creeper creeper ) {
		if( !( creeper.level instanceof ServerLevel ) )
			return null;

		ServerLevel world = ( ServerLevel )creeper.level;
		double x = creeper.getX(), y = creeper.getY(), z = creeper.getZ();
		AABB axisAlignedBB = new AABB( x - this.offset, y - this.offset, z - this.offset, x + this.offset, y + this.offset, z + this.offset );

		Player nearestPlayer = null;
		for( Player player : world.getEntitiesOfClass( Player.class, axisAlignedBB ) )
			if( !player.getAbilities().instabuild && ( nearestPlayer == null || creeper.distanceToSqr( player ) < creeper.distanceToSqr(
				nearestPlayer )
			) )
				nearestPlayer = player;

		return nearestPlayer;
	}
}

