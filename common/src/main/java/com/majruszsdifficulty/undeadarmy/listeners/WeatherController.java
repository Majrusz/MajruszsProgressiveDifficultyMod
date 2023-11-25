package com.majruszsdifficulty.undeadarmy.listeners;

import com.majruszlibrary.level.LevelHelper;
import com.majruszlibrary.time.TimeHelper;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyStarted;
import com.majruszsdifficulty.undeadarmy.events.OnUndeadArmyTicked;
import net.minecraft.world.entity.PathfinderMob;

public class WeatherController {
	static {
		OnUndeadArmyStarted.listen( WeatherController::startRaining );

		OnUndeadArmyTicked.listen( WeatherController::freezeWater );
	}

	private static void startRaining( OnUndeadArmyStarted data ) {
		LevelHelper.startRaining( data.getLevel(), TimeHelper.toTicks( 60.0 * 30.0 ), true );
	}

	private static void freezeWater( OnUndeadArmyTicked data ) {
		data.undeadArmy.mobsLeft.forEach( mobInfo->{
			if( mobInfo.toEntity( data.getServerLevel() ) instanceof PathfinderMob mob ) {
				LevelHelper.freezeWater( mob, 4.0, 30, 60, false );
			}
		} );
	}
}
