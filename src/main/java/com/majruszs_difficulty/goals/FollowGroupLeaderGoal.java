package com.majruszs_difficulty.goals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;

public class FollowGroupLeaderGoal extends Goal {
	protected final Mob follower;
	protected final Mob leader;
	protected final double speedModifier;
	protected final float maxDistanceFromLeader;
	protected final float stopDistance;
	private final PathNavigation navigation;
	protected int ticksToRecalculatePath;

	public FollowGroupLeaderGoal( Mob follower, Mob leader, double speedModifier, float maxDistanceFromLeader, float stopDistance ) {
		this.follower = follower;
		this.leader = leader;
		this.navigation = follower.getNavigation();
		this.speedModifier = speedModifier;
		this.maxDistanceFromLeader = maxDistanceFromLeader;
		this.stopDistance = stopDistance;
		this.ticksToRecalculatePath = 0;
	}

	@Override
	public boolean canUse() {
		return ( this.leader != null && this.leader.isAlive() && ( this.leader.distanceTo( this.follower ) >= this.maxDistanceFromLeader
		) && this.follower.getTarget() == null
		);
	}

	public void tick() {
		if( this.leader == null )
			return;

		if( --this.ticksToRecalculatePath > 0 )
			return;

		this.follower.getLookControl()
			.setLookAt( this.leader, 10.0F, ( float )this.follower.getHeadRotSpeed() );
		this.ticksToRecalculatePath = 10;
		double distance = this.follower.distanceTo( this.leader );

		if( distance <= ( double )( this.maxDistanceFromLeader ) )
			recalculatePath( distance );
		else
			this.navigation.moveTo( this.leader, this.speedModifier );
	}

	public boolean shouldContinueExecuting() {
		return this.leader != null && !this.navigation.isDone() && this.follower.distanceTo( this.leader ) > ( double )( this.stopDistance );
	}

	public void startExecuting() {
		this.ticksToRecalculatePath = 0;
	}

	protected void recalculatePath( double distance ) {
		this.navigation.stop();
		LookControl lookcontroller = this.leader.getLookControl();
		if( distance <= ( double )this.maxDistanceFromLeader || lookcontroller.getWantedX() == this.follower.getX() && lookcontroller.getWantedY() == this.follower.getY() && lookcontroller.getWantedZ() == this.follower.getZ() ) {
			double d4 = this.follower.getX() - this.leader.getX();
			double d5 = this.follower.getZ() - this.leader.getZ();
			this.navigation.moveTo( this.follower.getX() - d4, this.follower.getY(), this.follower.getZ() - d5, this.speedModifier );
		}
	}
}
