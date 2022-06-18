package com.majruszsdifficulty.features.monster_spawn;

import com.mlib.Utility;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

/** Handling all 'OnEnemySpawn' events. */
@Mod.EventBusSubscriber
public class OnEnemyToBeSpawnedEvent {
	public static final List< OnEnemyToBeSpawnedBase > REGISTRY_LIST = new ArrayList<>();
	private static final String MARK_TAG = "MajruszDifficultyEntityMarked";
	private static final int cooldown = Utility.secondsToTicks( 10.0 );
	private static int counter = 0;

	@SubscribeEvent
	public static void onSpawn( LivingSpawnEvent.SpecialSpawn event ) {
		LivingEntity entity = event.getEntityLiving();

		markEntity( entity );
		for( OnEnemyToBeSpawnedBase register : REGISTRY_LIST )
			if( register.shouldBeExecuted( entity ) ) {
				register.onExecute( entity, ( ServerLevel )entity.level );

				if( register.shouldSpawnBeCancelled() )
					event.setCanceled( true );
			}
	}

	@SubscribeEvent
	public static void onTick( TickEvent.WorldTickEvent event ) {
		if( !( event.world instanceof ServerLevel ) || event.phase == TickEvent.Phase.START )
			return;

		ServerLevel world = ( ServerLevel )event.world;
		counter++;
		if( counter % cooldown != 0 )
			return;

		LevelEntityGetter< Entity > entities = world.getEntities();
		for( Entity entity : entities.getAll() )
			if( entity instanceof LivingEntity && !isMarked( entity ) ) {
				LivingEntity livingEntity = ( LivingEntity )entity;

				markEntity( livingEntity );
				for( OnEnemyToBeSpawnedBase register : REGISTRY_LIST )
					if( register.shouldBeExecuted( livingEntity ) )
						register.onExecute( livingEntity, world );
			}
	}

	/** Makes entity marked. (adds tag to inform that given entity has all 'OnEnemySpawn' events handled) */
	private static void markEntity( Entity entity ) {
		CompoundTag data = entity.getPersistentData();
		data.putBoolean( MARK_TAG, true );
	}

	/** Returns whether entity is marked. */
	private static boolean isMarked( Entity entity ) {
		CompoundTag data = entity.getPersistentData();
		return data.contains( MARK_TAG ) && data.getBoolean( MARK_TAG );
	}
}
