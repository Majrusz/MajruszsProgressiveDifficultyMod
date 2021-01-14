package com.majruszs_difficulty.events;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.ConfigHandler.Config;
import com.majruszs_difficulty.entities.GiantEntity;
import com.majruszs_difficulty.events.monster_spawn.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class OnMonsterSpawn {
	@SubscribeEvent
	public static void onMonsterSpawn( LivingSpawnEvent.SpecialSpawn event ) {
		if( !MajruszsHelper.isHostile( event.getEntityLiving() ) || !( event.getWorld() instanceof ServerWorld ) )
			return;

		boostLivingEntity( event.getEntityLiving(), ( ServerWorld )event.getWorld() );
	}

	protected static void boostLivingEntity( LivingEntity livingEntity, ServerWorld world ) {
		if( livingEntity instanceof SkeletonEntity )
			boostSkeleton( ( SkeletonEntity )livingEntity, world );

		else if( livingEntity instanceof ZombieEntity && !( livingEntity instanceof GiantEntity ) && !( livingEntity instanceof ZombifiedPiglinEntity ) )
			boostZombie( ( ZombieEntity )livingEntity, world );

		else if( livingEntity instanceof PiglinEntity )
			boostPiglin( ( PiglinEntity )livingEntity, world );

		else if( livingEntity instanceof PillagerEntity )
			boostPillager( ( PillagerEntity )livingEntity, world );
	}

	protected static void boostSkeleton( SkeletonEntity skeleton, ServerWorld world ) {
		if( !Config.isDisabled( Config.Features.SKELETON_GROUPS ) )
			new SkeletonGroup( skeleton, world );
	}

	protected static void boostZombie( ZombieEntity zombie, ServerWorld world ) {
		if( !Config.isDisabled( Config.Features.ZOMBIE_GROUPS ) )
			new ZombieGroup( zombie, world );
	}

	protected static void boostPiglin( PiglinEntity piglin, ServerWorld world ) {
		if( GameState.atLeast( GameState.State.EXPERT ) && !Config.isDisabled( Config.Features.PIGLIN_GROUPS ) )
			new PiglinGroup( piglin, world );
	}

	protected static void boostPillager( PillagerEntity pillager, ServerWorld world ) {
		if( GameState.atLeast( GameState.State.EXPERT ) && !Config.isDisabled( Config.Features.PILLAGER_GROUPS ) )
			new PillagerGroup( pillager, world );
	}
}
