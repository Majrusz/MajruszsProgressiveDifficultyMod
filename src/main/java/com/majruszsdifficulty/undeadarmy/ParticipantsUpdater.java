package com.majruszsdifficulty.undeadarmy;

public class ParticipantsUpdater implements IComponent {
	final UndeadArmy undeadArmy;

	public ParticipantsUpdater( UndeadArmy undeadArmy ) {
		this.undeadArmy = undeadArmy;
	}

	@Override
	public void tick() {
		this.undeadArmy.participants.clear();
		this.undeadArmy.participants.addAll( this.undeadArmy.level.getPlayers( player->player.isAlive() && this.undeadArmy.isInRange( player.blockPosition() ) ) );
	}
}
