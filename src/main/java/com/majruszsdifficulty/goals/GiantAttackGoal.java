package com.majruszsdifficulty.goals;

import com.majruszsdifficulty.entities.GiantEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class GiantAttackGoal extends MeleeAttackGoal {
	private final GiantEntity giant;
	private int raiseArmTicks;

	public GiantAttackGoal( GiantEntity giant, double speedIn, boolean longMemoryIn ) {
		super( giant, speedIn, longMemoryIn );
		this.giant = giant;
	}

	public void startExecuting() {
		super.startExecuting();
		this.raiseArmTicks = 0;
	}

	public void resetTask() {
		super.resetTask();
		this.giant.setAggroed( false );
	}

	public void tick() {
		super.tick();
		++this.raiseArmTicks;
		this.giant.setAggroed( this.raiseArmTicks >= 5 && this.func_234041_j_() < this.func_234042_k_() / 2 );
	}

	@Override
	protected double getAttackReachSqr( LivingEntity attackTarget ) {
		return this.attacker.getWidth() * 0.9f * this.attacker.getWidth() * 0.9f + attackTarget.getWidth();
	}
}