package com.majruszs_difficulty.events;

import com.majruszs_difficulty.ConfigHandlerOld.Config;
import com.majruszs_difficulty.GameState;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Increasing experience from any source. */
@Mod.EventBusSubscriber
public class ExperienceBonus {
	public ExperienceBonus() {

	}
	
	@SubscribeEvent
	public static void onXPPickUp( PlayerXpEvent.PickupXp event ) {
		ExperienceOrbEntity orb = event.getOrb();
		int bonusExperience = ( int )( Math.round( getExperienceMultiplier() * ( double )orb.getXpValue() ) );

		if( bonusExperience <= 0 )
			return;

		PlayerEntity player = event.getPlayer();
		player.giveExperiencePoints( bonusExperience );
	}

	/** Returns extra experience depending on current game state. */
	private static double getExperienceMultiplier() {
		return GameState.getDoubleDependingOnGameState( Config.Values.EXPERIENCE_BONUS_NORMAL, Config.Values.EXPERIENCE_BONUS_EXPERT,
			Config.Values.EXPERIENCE_BONUS_MASTER
		);
	}
}
