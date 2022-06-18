package com.majruszsdifficulty.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;

public class UndeadAttackPositionGoal extends Goal {
	protected final Mob undead;
	protected final BlockPos attackPosition;
	protected final double speedModifier;
	protected final float maxDistanceFromPosition;
	protected final float stopDistance;
	private final PathNavigation navigation;
	protected int ticksToRecalculatePath;

	public UndeadAttackPositionGoal( Mob undead, BlockPos attackPosition, double speedModifier, float maxDistanceFromPosition, float stopDistance
	) {
		this.undead = undead;
		this.navigation = undead.getNavigation();
		this.attackPosition = attackPosition;
		this.speedModifier = speedModifier;
		this.maxDistanceFromPosition = maxDistanceFromPosition;
		this.stopDistance = stopDistance;
		this.ticksToRecalculatePath = 0;
	}

	@Override
	public boolean canUse() {
		return !isInRadius() && !hasAnyTarget();
	}

	@Override
	public boolean canContinueToUse() {
		return !isInRadius() && !this.navigation.isDone() && !hasAnyTarget();
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

	protected boolean isInRadius() {
		return getDistanceToAttackPosition() < this.maxDistanceFromPosition;
	}

	protected boolean hasAnyTarget() {
		return this.undead.getTarget() != null || this.undead.getLastHurtByMob() != null;
	}

	protected double getDistanceToAttackPosition() {
		return Math.sqrt( Math.pow( this.undead.getX() - attackPosition.getX(), 2 ) + Math.pow( this.undead.getZ() - attackPosition.getZ(), 2 ) );
	}
}

