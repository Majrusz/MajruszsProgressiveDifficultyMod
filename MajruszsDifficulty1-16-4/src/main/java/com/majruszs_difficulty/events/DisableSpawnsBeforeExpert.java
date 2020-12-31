package com.majruszs_difficulty.events;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.GameState.Mode;
import com.majruszs_difficulty.ConfigHandler.Config;
import com.majruszs_difficulty.entities.EliteSkeletonEntity;
import com.majruszs_difficulty.entities.GiantEntity;
import com.majruszs_difficulty.entities.PillagerWolfEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IllusionerEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class DisableSpawnsBeforeExpert {
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

		if( isGiant )
			return shouldBeDisabled( Mode.EXPERT, Config.Features.GIANT_SPAWNING );
		else if( isIllusioner )
			return shouldBeDisabled( Mode.EXPERT, Config.Features.ILLUSIONER_SPAWNING );
		else if( isPillagerWolf )
			return shouldBeDisabled( Mode.EXPERT, Config.Features.PILLAGER_WOLF_SPAWNING );
		else if( isEliteSkeleton )
			return shouldBeDisabled( Mode.EXPERT, Config.Features.PILLAGER_WOLF_SPAWNING );
		else
			return false;
	}

	private static boolean shouldBeDisabled( GameState.Mode minimumMode, ForgeConfigSpec.BooleanValue config ) {
		return !GameState.atLeast( minimumMode ) || Config.isDisabled( config );
	}
}
