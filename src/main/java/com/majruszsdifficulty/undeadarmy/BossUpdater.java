package com.majruszsdifficulty.undeadarmy;

record BossUpdater( UndeadArmy undeadArmy ) implements IComponent {
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
