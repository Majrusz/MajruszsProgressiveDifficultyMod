package com.majruszs_difficulty.events;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.entities.GiantEntity;
import com.majruszs_difficulty.entities.PillagerWolfEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IllusionerEntity;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class DisableSpawnsBeforeExpert {
	@SubscribeEvent
	public static void disableSpawns( LivingSpawnEvent.CheckSpawn event ) {
		if( GameState.atLeast( GameState.Mode.EXPERT ) )
			return;

		LivingEntity entity = event.getEntityLiving();

		if( entity instanceof GiantEntity || entity instanceof IllusionerEntity || entity instanceof PillagerWolfEntity )
			event.setResult( Event.Result.DENY );

	}
}
