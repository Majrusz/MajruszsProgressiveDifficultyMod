package com.majruszsdifficulty.undeadarmy;

import com.majruszsdifficulty.Registries;

record AdvancementsController( UndeadArmy undeadArmy ) implements IComponent {
	@Override
	public void onWaveFinished() {
		if( this.undeadArmy.isLastWave() ) {
			this.undeadArmy.participants.forEach( participant->Registries.BASIC_TRIGGER.trigger( participant, "army_defeated" ) );
		}
	}
}
