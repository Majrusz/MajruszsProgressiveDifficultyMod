package com.majruszsdifficulty.goals;

import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

/** Removes mob's target when both the mob and the target are from the Undead Army. */
public class ForgiveUndeadArmyTargetGoal extends HurtByTargetGoal {
	public ForgiveUndeadArmyTargetGoal( PathfinderMob mob, Class< ? >... ignoredClasses ) {
		super( mob, ignoredClasses );
	}

	public boolean canUse() {
		return this.mob.getLastHurtByMob() != null && !( UndeadArmy.doesEntityBelongToUndeadArmy( this.mob ) && UndeadArmy.doesEntityBelongToUndeadArmy( this.mob.getLastHurtByMob() )
		) && super.canUse();
	}
}
