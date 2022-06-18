package com.majruszsdifficulty.features.undead_army;

import com.majruszsdifficulty.RegistryHandler;
import com.mlib.Utility;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Reloading goals for Undead Army after reloading world. */
@Mod.EventBusSubscriber
public class ReloadUndeadArmyGoals {
	private static long COUNTER = -1L;

	@SubscribeEvent
	public static void onUpdate( TickEvent.WorldTickEvent event ) {
		if( COUNTER == -1L )
			return;

		COUNTER++;

		if( RegistryHandler.UNDEAD_ARMY_MANAGER != null && COUNTER % Utility.secondsToTicks( 25.0 ) == 0L ) {
			RegistryHandler.UNDEAD_ARMY_MANAGER.updateUndeadAIGoals();
			COUNTER = -1L;
		}
	}

	public static void resetTimer() {
		COUNTER = 0L;
	}
}
