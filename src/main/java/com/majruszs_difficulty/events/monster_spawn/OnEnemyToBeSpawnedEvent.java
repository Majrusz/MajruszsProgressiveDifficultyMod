package com.majruszs_difficulty.events.monster_spawn;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

/** Handling all 'OnEnemySpawn' events. */
@Mod.EventBusSubscriber
public class OnEnemyToBeSpawnedEvent {
	public static final List< OnEnemyToBeSpawnedBase > REGISTRY_LIST = new ArrayList<>();

	@SubscribeEvent
	public static void onSpawn( LivingSpawnEvent.SpecialSpawn event ) {
		LivingEntity entity = event.getEntityLiving();

		for( OnEnemyToBeSpawnedBase register : REGISTRY_LIST )
			if( register.shouldBeExecuted( entity ) ) {
				register.onExecute( entity, ( ServerWorld )entity.world );

				if( register.shouldSpawnBeCancelled() )
					event.setCanceled( true );
			}
	}
}
