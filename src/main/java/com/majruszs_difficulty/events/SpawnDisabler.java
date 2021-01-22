package com.majruszs_difficulty.events;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.GameState.State;
import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.entities.EliteSkeletonEntity;
import com.majruszs_difficulty.entities.GiantEntity;
import com.majruszs_difficulty.entities.PillagerWolfEntity;
import com.majruszs_difficulty.entities.SkyKeeperEntity;
import com.mlib.config.AvailabilityConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IllusionerEntity;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SpawnDisabler {
	@SubscribeEvent
	public static void disableSpawns( LivingSpawnEvent.CheckSpawn event ) {
		if( isEntityToBeDisabled( event.getEntityLiving() ) )
			event.setResult( Event.Result.DENY );
	}

	protected static boolean isEntityToBeDisabled( Entity entity ) {
		boolean isGiant = entity instanceof GiantEntity;
		boolean isIllusioner = entity instanceof IllusionerEntity;
		boolean isPillagerWolf = entity instanceof PillagerWolfEntity;
		boolean isEliteSkeleton = entity instanceof EliteSkeletonEntity;
		boolean isSkyKeeper = entity instanceof SkyKeeperEntity;

		if( isGiant )
			return shouldBeDisabled( GameState.State.EXPERT, Instances.ENTITIES_CONFIG.giant.availability );
		else if( isIllusioner )
			return shouldBeDisabled( GameState.State.EXPERT, Instances.ENTITIES_CONFIG.illusioner.availability );
		else if( isPillagerWolf )
			return shouldBeDisabled( State.EXPERT, Instances.ENTITIES_CONFIG.pillagerWolf.availability );
		else if( isEliteSkeleton )
			return shouldBeDisabled( GameState.State.EXPERT, Instances.ENTITIES_CONFIG.pillagerWolf.availability );
		else if( isSkyKeeper )
			return shouldBeDisabled( GameState.State.EXPERT, Instances.ENTITIES_CONFIG.skyKeeper.availability );
		else
			return false;
	}

	private static boolean shouldBeDisabled( GameState.State minimumState, AvailabilityConfig config ) {
		return !GameState.atLeast( minimumState ) || config.isDisabled();
	}
}
