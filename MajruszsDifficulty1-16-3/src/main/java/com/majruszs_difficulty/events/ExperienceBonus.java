package com.majruszs_difficulty.events;

import com.majruszs_difficulty.GameState;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ExperienceBonus {
	public static final double experienceMultiplier = 0.25;

	@SubscribeEvent
	public static void onXPPickUp( PlayerXpEvent.PickupXp event ) {
		ExperienceOrbEntity orb = event.getOrb();
		int bonusExp = ( int )( Math.round( experienceMultiplier * ( double )orb.getXpValue() ) );

		if( !( bonusExp > 0 ) )
			return;

		PlayerEntity player = event.getPlayer();
		player.giveExperiencePoints( ( GameState.atLeast( GameState.Mode.MASTER ) ? 2 : 1 ) * bonusExp );
	}
}
