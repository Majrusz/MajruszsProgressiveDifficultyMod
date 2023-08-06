package com.majruszsdifficulty.goals;

import com.majruszsdifficulty.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

public class UndeadArmyForgiveTeammateGoal extends Goal {
	final PathfinderMob mob;

	public UndeadArmyForgiveTeammateGoal( PathfinderMob mob ) {
		this.mob = mob;
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		this.mob.setTarget( null );
		this.mob.targetSelector.getRunningGoals()
			.filter( wrappedGoal->wrappedGoal.getGoal() instanceof HurtByTargetGoal )
			.map( goal->( HurtByTargetGoal )goal.getGoal() )
			.forEach( HurtByTargetGoal::stop );
	}

	@Override
	public boolean canUse() {
		return this.mob.getTarget() != null
			&& this.areFromUndeadArmy( this.mob, this.mob.getTarget() );
	}

	private boolean areFromUndeadArmy( LivingEntity target, LivingEntity attacker ) {
		return Registries.getUndeadArmyManager().isPartOfUndeadArmy( target )
			&& Registries.getUndeadArmyManager().isPartOfUndeadArmy( attacker );
	}
}
