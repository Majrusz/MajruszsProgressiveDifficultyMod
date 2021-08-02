package com.majruszs_difficulty.goals;

import com.majruszs_difficulty.entities.GiantEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class GiantAttackGoal extends MeleeAttackGoal {
	private final GiantEntity giant;
	private int raiseArmTicks;

	public GiantAttackGoal( GiantEntity giant, double speedIn, boolean longMemoryIn ) {
		super( giant, speedIn, longMemoryIn );
		this.giant = giant;
	}

	@Override
	public void start() {
		super.start();
		this.raiseArmTicks = 0;
	}

	@Override
	public void stop() {
		super.stop();
		this.giant.setAggressive( false );
	}

	@Override
	public void tick() {
		super.tick();
		++this.raiseArmTicks;
		this.giant.setAggressive( this.raiseArmTicks >= 5 && this.getTicksUntilNextAttack() < this.getAttackInterval() / 2 );
	}

	@Override
	protected double getAttackReachSqr( LivingEntity attackTarget ) {
		return this.mob.getBbWidth() * 0.9f * this.mob.getBbWidth() * 0.9f + attackTarget.getBbWidth();
	}
}