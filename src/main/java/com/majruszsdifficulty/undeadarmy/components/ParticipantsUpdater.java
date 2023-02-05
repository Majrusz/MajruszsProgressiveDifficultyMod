package com.majruszsdifficulty.undeadarmy.components;

import com.majruszsdifficulty.undeadarmy.UndeadArmy;

record ParticipantsUpdater( UndeadArmy undeadArmy ) implements IComponent {
	@Override
	public void tick() {
		this.undeadArmy.participants.clear();
		this.undeadArmy.participants.addAll( this.undeadArmy.level.getPlayers( player->player.isAlive() && this.undeadArmy.isInRange( player.blockPosition() ) ) );
	}
}
