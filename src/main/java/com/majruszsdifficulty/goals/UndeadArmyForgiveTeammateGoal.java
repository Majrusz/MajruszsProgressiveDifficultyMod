package com.majruszsdifficulty.goals;

import com.majruszsdifficulty.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

/** Removes mob's target when both the mob and the target are from the Undead Army. */
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
