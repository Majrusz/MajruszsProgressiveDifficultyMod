package com.majruszsdifficulty.goals;

import com.mlib.math.VectorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;

public class UndeadArmyAttackPositionGoal extends Goal {
	final Mob undead;
	final BlockPos attackPosition;
	final PathNavigation navigation;
	final float speedModifier = 1.25f;
	final float maxDistanceFromPosition = 20.0f;
	int ticksToRecalculatePath = 0;

	public UndeadArmyAttackPositionGoal( Mob mob, BlockPos attackPosition ) {
		this.undead = mob;
		this.navigation = mob.getNavigation();
		this.attackPosition = attackPosition;
	}

	@Override
	public boolean canUse() {
		return !this.isInRadius()
			&& !this.hasAnyTarget();
	}

	@Override
	public boolean canContinueToUse() {
		return !this.canUse()
			&& !this.navigation.isDone();
	}

	@Override
	public void start() {
		this.ticksToRecalculatePath = 0;
	}

	@Override
	public void tick() {
		if( --this.ticksToRecalculatePath > 0 )
			return;

		this.ticksToRecalculatePath = 10;
		this.navigation.moveTo( this.attackPosition.getX(), this.attackPosition.getY(), this.attackPosition.getZ(), this.speedModifier );
	}

	private boolean isInRadius() {
		return this.getDistanceToAttackPosition() < this.maxDistanceFromPosition;
	}

	private boolean hasAnyTarget() {
		return this.undead.getTarget() != null || this.undead.getLastHurtByMob() != null;
	}

	private double getDistanceToAttackPosition() {
		return VectorHelper.distanceHorizontal( this.undead.position(), this.attackPosition.getCenter() );
	}
}

