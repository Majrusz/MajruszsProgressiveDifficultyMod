package com.majruszsdifficulty.bloodmoon.listeners;

import com.majruszlibrary.events.OnServerTicked;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.platform.Side;
import com.majruszsdifficulty.bloodmoon.BloodMoonConfig;
import com.majruszsdifficulty.bloodmoon.BloodMoonHelper;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class Trigger {
	static {
		OnServerTicked.listen( Trigger::start )
			.addCondition( data->BloodMoonConfig.IS_ENABLED )
			.addCondition( data->Trigger.getTime() == BloodMoonConfig.TIME.from )
			.addCondition( Condition.chance( ()->BloodMoonConfig.NIGHT_TRIGGER_CHANCE ) );

		OnServerTicked.listen( Trigger::finish )
			.addCondition( data->BloodMoonConfig.IS_ENABLED )
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
		return Optional.ofNullable( Side.getServer() ).map( server->server.overworld().getDayTime() % Level.TICKS_PER_DAY ).orElse( 0L );
	}
}
