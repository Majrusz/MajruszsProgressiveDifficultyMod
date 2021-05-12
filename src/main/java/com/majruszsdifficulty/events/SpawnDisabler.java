package com.majruszsdifficulty.events;

import com.majruszsdifficulty.GameState;
import com.majruszsdifficulty.Instances;
import com.majruszsdifficulty.entities.EliteSkeletonEntity;
import com.majruszsdifficulty.entities.GiantEntity;
import com.majruszsdifficulty.entities.PillagerWolfEntity;
import com.majruszsdifficulty.entities.SkyKeeperEntity;
import com.mlib.config.AvailabilityConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IllusionerEntity;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Disabling natural spawns for entities that have not met certain conditions. */
@Mod.EventBusSubscriber
public class SpawnDisabler {
	@SubscribeEvent
	public static void disableSpawns( LivingSpawnEvent.CheckSpawn event ) {
		if( shouldEntitySpawnBeDisabled( event.getEntityLiving() ) )
			event.setResult( Event.Result.DENY );
	}

	/** Checks whether given entity should not spawn. */
	protected static boolean shouldEntitySpawnBeDisabled( Entity entity ) {
		boolean isGiant = entity instanceof GiantEntity;
		boolean isIllusioner = entity instanceof IllusionerEntity;
		boolean isPillagerWolf = entity instanceof PillagerWolfEntity;
		boolean isEliteSkeleton = entity instanceof EliteSkeletonEntity;
		boolean isSkyKeeper = entity instanceof SkyKeeperEntity;

		if( isGiant )
			return shouldBeDisabled( GameState.State.EXPERT, Instances.ENTITIES_CONFIG.giant.availability );
		else if( isIllusioner )
			return shouldBeDisabled( GameState.State.EXPERT, Instances.ENTITIES_CONFIG.illusioner.availability ) || isVillageNearby( entity );
		else if( isPillagerWolf )
			return shouldBeDisabled( GameState.State.EXPERT, Instances.ENTITIES_CONFIG.pillagerWolf.availability );
		else if( isEliteSkeleton )
			return shouldBeDisabled( GameState.State.EXPERT, Instances.ENTITIES_CONFIG.eliteSkeleton.availability );
		else if( isSkyKeeper )
			return shouldBeDisabled( GameState.State.EXPERT, Instances.ENTITIES_CONFIG.skyKeeper.availability );
		else
			return false;
	}

	/** Checks most common variant for disabling. */
	private static boolean shouldBeDisabled( GameState.State minimumState, AvailabilityConfig config ) {
		return !GameState.atLeast( minimumState ) || config.isDisabled();
	}

	/**
	 Checks whether there is the village nearby.
	 It is required to disable spawns near village for Illusioner because
	 otherwise Illusioner may spawn underground while there is raid active and
	 players will have a problem to finish it.
	 */
	private static boolean isVillageNearby( Entity entity ) {
		if( !( entity.world instanceof ServerWorld ) )
			return false;

		ServerWorld world = ( ServerWorld )entity.world;
		return world.func_241117_a_( Structure.VILLAGE, entity.getPosition(), 10000, false ) != null;
	}
}
