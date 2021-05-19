package com.majruszs_difficulty.goals;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;

public class UndeadAttackPositionGoal extends Goal {
	protected final MobEntity undead;
	protected final BlockPos attackPosition;
	protected final double speedModifier;
	protected final float maxDistanceFromPosition;
	protected final float stopDistance;
	private final PathNavigator navigation;
	protected int ticksToRecalculatePath;

	public UndeadAttackPositionGoal( MobEntity undead, BlockPos attackPosition, double speedModifier, float maxDistanceFromPosition,
		float stopDistance
	) {
		this.undead = undead;
		this.navigation = undead.getNavigator();
		this.attackPosition = attackPosition;
		this.speedModifier = speedModifier;
		this.maxDistanceFromPosition = maxDistanceFromPosition;
		this.stopDistance = stopDistance;
		this.ticksToRecalculatePath = 0;
	}

	@Override
	public boolean shouldExecute() {
		return !isInRadius() && !hasAnyTarget();
	}

	@Override
	public boolean shouldContinueExecuting() {
		return !isInRadius() && !this.navigation.noPath() && !hasAnyTarget();
	}

	public void startExecuting() {
		this.ticksToRecalculatePath = 0;
	}

	public void tick() {
		if( --this.ticksToRecalculatePath > 0 )
			return;

		this.ticksToRecalculatePath = 10;
		this.navigation.tryMoveToXYZ( this.attackPosition.getX(), this.attackPosition.getY(), this.attackPosition.getZ(), this.speedModifier );
	}

	protected boolean isInRadius() {
		return getDistanceToAttackPosition() < this.maxDistanceFromPosition;
	}

	protected boolean hasAnyTarget() {
		return this.undead.getAttackTarget() != null || this.undead.getRevengeTarget() != null;
	}

	protected double getDistanceToAttackPosition() {
		return Math.sqrt(
			Math.pow( this.undead.getPosX() - attackPosition.getX(), 2 ) + Math.pow( this.undead.getPosZ() - attackPosition.getZ(), 2 ) );
	}
}

