package com.majruszsdifficulty.goals;

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

	@Override
	public void tick() {
		if( this.leader == null )
			return;

		if( --this.ticksToRecalculatePath > 0 )
			return;

		this.follower.getLookControl().setLookAt( this.leader, 10.0F, ( float )this.follower.getHeadRotSpeed() );
		this.ticksToRecalculatePath = 20;
		this.navigation.moveTo( this.leader, this.speedModifier );
	}

	@Override
	public boolean canContinueToUse() {
		return this.leader != null && !this.navigation.isDone() && this.follower.distanceTo( this.leader ) > ( double )( this.stopDistance );
	}

	@Override
	public void start() {
		this.ticksToRecalculatePath = 0;
	}
}
