package com.majruszsdifficulty.goals;

import com.majruszsdifficulty.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

/** Removes mob's target when both the mob and the target are from the Undead Army. */
public class UndeadArmyForgiveTeammateGoal extends HurtByTargetGoal {
	public UndeadArmyForgiveTeammateGoal( PathfinderMob mob, Class< ? >... ignoredClasses ) {
		super( mob, ignoredClasses );
	}

	public boolean canUse() {
		return this.mob.getLastHurtByMob() != null
			&& !this.areFromTheSameTeam( this.mob, this.mob.getLastHurtByMob() )
			&& super.canUse();
	}

	private boolean areFromTheSameTeam( LivingEntity target, LivingEntity attacker ) {
		return Registries.getUndeadArmyManager().isPartOfUndeadArmy( target )
			&& Registries.getUndeadArmyManager().isPartOfUndeadArmy( attacker );
	}
}
