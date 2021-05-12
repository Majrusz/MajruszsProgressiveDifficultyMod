package com.majruszsdifficulty.goals;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.ai.goal.TargetGoal;

public class TargetAsLeaderGoal extends TargetGoal {
	private static final EntityPredicate predicate = ( new EntityPredicate() ).setLineOfSiteRequired()
		.setUseInvisibilityCheck();
	protected final CreatureEntity leader;

	public TargetAsLeaderGoal( CreatureEntity follower, CreatureEntity leader ) {
		super( follower, false );
		this.leader = leader;
	}

	@Override
	public boolean shouldExecute() {
		return this.leader != null && this.leader.isAlive() && this.isSuitableTarget( this.leader.getAttackTarget(),
			predicate
		) && ( this.leader.getAttackTarget() != this.goalOwner.getAttackTarget() );
	}

	@Override
	public void startExecuting() {
		this.goalOwner.setAttackTarget( this.leader.getAttackTarget() );
		this.target = this.leader.getAttackTarget();
		this.unseenMemoryTicks = 300;

		super.startExecuting();
	}
}
