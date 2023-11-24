package com.majruszsdifficulty.bloodmoon.listeners;

import com.majruszlibrary.events.OnServerTicked;
import com.majruszlibrary.platform.Side;
import com.majruszsdifficulty.bloodmoon.BloodMoonConfig;
import com.majruszsdifficulty.bloodmoon.BloodMoonHelper;
import net.minecraft.world.level.Level;

public class Trigger {
	static {
		OnServerTicked.listen( Trigger::start )
			.addCondition( data->Trigger.getTime() == BloodMoonConfig.TIME.from );

		OnServerTicked.listen( Trigger::finish )
			.addCondition( data->!BloodMoonConfig.TIME.within( Trigger.getTime() ) )
			.addCondition( data->BloodMoonHelper.isActive() );
	}

	private static void start( OnServerTicked data ) {
		BloodMoonHelper.start();
	}

	private static void finish( OnServerTicked data ) {
		BloodMoonHelper.stop();
	}

	private static long getTime() {
		return Side.getServer().overworld().getDayTime() % Level.TICKS_PER_DAY;
	}
}
