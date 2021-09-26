package com.majruszs_difficulty.goals;

import com.majruszs_difficulty.entities.TankEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class TankAttackGoal extends MeleeAttackGoal {
	private int raiseArmTicks;

	public TankAttackGoal( TankEntity tank ) {
		super( tank, 1.0, true );
	}

	public void start() {
		super.start();
		this.raiseArmTicks = 0;
	}

	public void stop() {
		super.stop();
		this.mob.setAggressive( false );
	}

	public void tick() {
		super.tick();
		++this.raiseArmTicks;
		this.mob.setAggressive( this.raiseArmTicks >= 5 && this.getTicksUntilNextAttack() < this.getAttackInterval() / 2 );
	}
}