package com.majruszsdifficulty.bloodmoon.listeners;

import com.majruszlibrary.events.OnServerTicked;
import com.majruszlibrary.events.base.Condition;
import com.majruszsdifficulty.bloodmoon.BloodMoonConfig;
import com.majruszsdifficulty.bloodmoon.BloodMoonHelper;

public class Trigger {
	static {
		OnServerTicked.listen( Trigger::start )
			.addCondition( data->BloodMoonConfig.IS_ENABLED )
			.addCondition( data->BloodMoonHelper.getRelativeDayTime() == BloodMoonConfig.TIME.from )
			.addCondition( Condition.chance( ()->BloodMoonConfig.NIGHT_TRIGGER_CHANCE ) );

		OnServerTicked.listen( Trigger::finish )
			.addCondition( data->BloodMoonConfig.IS_ENABLED )
			.addCondition( data->!BloodMoonHelper.isValidDayTime() )
			.addCondition( data->BloodMoonHelper.isActive() );
	}

	private static void start( OnServerTicked data ) {
		BloodMoonHelper.start();
	}

	private static void finish( OnServerTicked data ) {
		BloodMoonHelper.stop();
	}
}
