package com.majruszs_difficulty.goals;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.PathNavigator;

public class FollowGroupLeaderGoal extends Goal {
	protected final MobEntity follower;
	protected final MobEntity leader;
	private final PathNavigator navigation;
	protected final double speedModifier;
	protected final float maxDistanceFromLeader;
	protected final float stopDistance;
	protected int ticksToRecalculatePath;

	public FollowGroupLeaderGoal( MobEntity follower, MobEntity leader, double speedModifier, float maxDistanceFromLeader, float stopDistance ) {
		this.follower = follower;
		this.leader = leader;
		this.navigation = follower.getNavigator();
		this.speedModifier = speedModifier;
		this.maxDistanceFromLeader = maxDistanceFromLeader;
		this.stopDistance = stopDistance;
		this.ticksToRecalculatePath = 0;
	}

	@Override
	public boolean shouldExecute() {
		return ( this.leader != null && this.leader.isAlive() && ( this.leader.getDistance( this.follower ) >= this.maxDistanceFromLeader ) && this.follower.getAttackTarget() == null );
	}

	public boolean shouldContinueExecuting() {
		return this.leader != null && !this.navigation.noPath() && this.follower.getDistance( this.leader ) > ( double )( this.stopDistance );
	}

	public void startExecuting() {
		this.ticksToRecalculatePath = 0;
	}

	public void tick() {
		if( this.leader == null )
			return;

		if( --this.ticksToRecalculatePath > 0 )
			return;

		this.follower.getLookController()
			.setLookPositionWithEntity( this.leader, 10.0F, ( float )this.follower.getVerticalFaceSpeed() );
		this.ticksToRecalculatePath = 10;
		double distance = this.follower.getDistance( this.leader );

		if( distance <= ( double )( this.maxDistanceFromLeader ) )
			recalculatePath( distance );
		else
			this.navigation.tryMoveToEntityLiving( this.leader, this.speedModifier );
	}

	protected void recalculatePath( double distance ) {
		this.navigation.clearPath();
		LookController lookcontroller = this.leader.getLookController();
		if( distance <= ( double )this.maxDistanceFromLeader || lookcontroller.getLookPosX() == this.follower.getPosX() && lookcontroller.getLookPosY() == this.follower.getPosY() && lookcontroller.getLookPosZ() == this.follower.getPosZ() ) {
			double d4 = this.follower.getPosX() - this.leader.getPosX();
			double d5 = this.follower.getPosZ() - this.leader.getPosZ();
			this.navigation.tryMoveToXYZ( this.follower.getPosX() - d4, this.follower.getPosY(), this.follower.getPosZ() - d5,
				this.speedModifier
			);
		}
	}
}
