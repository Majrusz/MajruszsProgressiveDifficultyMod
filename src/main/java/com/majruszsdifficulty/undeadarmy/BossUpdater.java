package com.majruszsdifficulty.undeadarmy;

public class BossUpdater implements IComponent {
	final UndeadArmy undeadArmy;

	public BossUpdater( UndeadArmy undeadArmy ) {
		this.undeadArmy = undeadArmy;
	}

	@Override
	public void tick() {
		for( MobInfo mobInfo : this.undeadArmy.mobsLeft ) {
			if( mobInfo.isBoss && mobInfo.uuid != null ) {
				this.undeadArmy.boss = mobInfo.toEntity( this.undeadArmy.level );
				return;
			}
		}

		this.undeadArmy.boss = null;
	}
}
