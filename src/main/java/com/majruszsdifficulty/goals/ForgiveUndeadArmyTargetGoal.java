package com.majruszsdifficulty.goals;

import com.majruszsdifficulty.undeadarmy.UndeadArmyManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

/** Removes mob's target when both the mob and the target are from the Undead Army. */
public class ForgiveUndeadArmyTargetGoal extends HurtByTargetGoal {
	public ForgiveUndeadArmyTargetGoal( PathfinderMob mob, Class< ? >... ignoredClasses ) {
		super( mob, ignoredClasses );
	}

	public boolean canUse() {
		return this.mob.getLastHurtByMob() != null && !this.areFromTheSameTeam( this.mob, this.mob.getLastHurtByMob() ) && super.canUse();
	}

	private boolean areFromTheSameTeam( LivingEntity target, LivingEntity attacker ) {
		if( UndeadArmyManager.belongsToUndeadArmy( target ) ) {
			return UndeadArmyManager.belongsToUndeadArmy( attacker );
		} else if( UndeadArmyManager.belongsToUndeadArmyPatrol( target ) ) {
			return UndeadArmyManager.belongsToUndeadArmyPatrol( attacker );
		}

		return false;
	}
}
