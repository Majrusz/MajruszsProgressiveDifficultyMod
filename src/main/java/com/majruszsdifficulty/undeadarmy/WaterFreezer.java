package com.majruszsdifficulty.undeadarmy;

import com.mlib.levels.LevelHelper;
import net.minecraft.world.entity.PathfinderMob;

record WaterFreezer( UndeadArmy undeadArmy ) implements IComponent {
	@Override
	public void tick() {
		this.undeadArmy.mobsLeft.forEach( mobInfo->{
			if( mobInfo.toEntity( this.undeadArmy.level ) instanceof PathfinderMob mob ) {
				LevelHelper.freezeWater( mob, 4.0, 30, 60, false );
			}
		} );
	}
}
