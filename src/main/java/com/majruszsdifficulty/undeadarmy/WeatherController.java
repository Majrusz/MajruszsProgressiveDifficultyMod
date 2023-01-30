package com.majruszsdifficulty.undeadarmy;

import com.mlib.Utility;
import com.mlib.levels.LevelHelper;
import net.minecraft.world.entity.PathfinderMob;

record WeatherController( UndeadArmy undeadArmy ) implements IComponent {
	@Override
	public void tick() {
		this.undeadArmy.mobsLeft.forEach( mobInfo->{
			if( mobInfo.toEntity( this.undeadArmy.level ) instanceof PathfinderMob mob ) {
				LevelHelper.freezeWater( mob, 4.0, 30, 60, false );
			}
		} );
	}

	@Override
	public void onStart() {
		LevelHelper.startRaining( this.undeadArmy.level, Utility.minutesToTicks( 30.0 ), true );
	}
}
