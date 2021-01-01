package com.majruszs_difficulty.events.undead_army;

import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.RegistryHandler;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ReloadUndeadArmyGoals {
	private static long counter = -1L;

	@SubscribeEvent
	public static void onUpdate( TickEvent.WorldTickEvent event ) {
		if( counter == -1L )
			return;

		counter++;

		if( counter % MajruszsHelper.secondsToTicks( 25.0 ) == 0L ) {
			RegistryHandler.undeadArmyManager.updateUndeadGoals();
			counter = -1L;
		}
	}

	public static void resetTimer() {
		counter = 0L;
	}
}
