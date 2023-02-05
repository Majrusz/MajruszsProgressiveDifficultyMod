package com.majruszsdifficulty.undeadarmy.components;

import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.majruszsdifficulty.undeadarmy.data.Phase;
import com.mlib.time.TimeHelper;

record MobHighlighter( UndeadArmy undeadArmy ) implements IComponent {
	@Override
	public void tick() {
		if( this.shouldHighlightArmy() ) {
			this.undeadArmy.highlightArmy();
		}
	}

	private boolean shouldHighlightArmy() {
		return TimeHelper.hasServerSecondsPassed( 4.0 )
			&& this.undeadArmy.phase.state == Phase.State.WAVE_ONGOING
			&& this.undeadArmy.phase.getTicksActive() >= this.undeadArmy.config.getHighlightDelay();
	}
}
