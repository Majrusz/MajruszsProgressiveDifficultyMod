package com.majruszsdifficulty.goals;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class TargetAsLeaderGoal extends TargetGoal {
	private static final TargetingConditions predicate = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
	protected final PathfinderMob leader;

	public TargetAsLeaderGoal( PathfinderMob follower, PathfinderMob leader ) {
		super( follower, false );
		this.leader = leader;
	}

	@Override
	public boolean canUse() {
		return this.leader != null && this.leader.isAlive() && this.canAttack( this.leader.getTarget(),
			predicate
		) && ( this.leader.getTarget() != this.mob.getTarget() );
	}

	@Override
	public void start() {
		this.mob.setTarget( this.leader.getTarget() );
		this.targetMob = this.leader.getTarget();
		this.unseenMemoryTicks = 300;

		super.start();
	}
}
