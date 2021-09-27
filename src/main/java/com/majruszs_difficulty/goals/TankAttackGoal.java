package com.majruszs_difficulty.goals;

import com.majruszs_difficulty.entities.TankEntity;
import com.mlib.Random;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class TankAttackGoal extends MeleeAttackGoal {
	private final TankEntity tank;
	private int delayTicks;

	public TankAttackGoal( TankEntity tank ) {
		super( tank, 1.0, true );
		this.tank = tank;
	}

	public void start() {
		super.start();
		this.delayTicks = 0;
	}

	public void stop() {
		super.stop();
		this.tank.setAggressive( false );
	}

	public void tick() {
		super.tick();
		++this.delayTicks;
		this.tank.setAggressive( !this.tank.isAttacking() && this.delayTicks >= 10 && getTicksUntilNextAttack() < getAttackInterval() / 2 );
	}

	@Override
	protected void checkAndPerformAttack( LivingEntity entity, double distance ) {
		if( distance <= getAttackReachSqr( entity ) && isTimeToAttack() && !this.tank.isAttacking() ) {
			this.tank.useAttack( Random.tryChance( 0.25 ) ? TankEntity.AttackType.SPECIAL : TankEntity.AttackType.NORMAL );
		} else if( this.tank.isAttackLastTick() ) {
			this.resetAttackCooldown();
		}
	}
}