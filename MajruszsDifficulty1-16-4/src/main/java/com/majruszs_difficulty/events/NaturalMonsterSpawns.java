package com.majruszs_difficulty.events;

import com.majruszs_difficulty.entities.EliteSkeletonEntity;
import com.majruszs_difficulty.entities.GiantEntity;
import com.majruszs_difficulty.entities.PillagerWolfEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class NaturalMonsterSpawns {
	@SubscribeEvent( priority = EventPriority.HIGH )
	public static void addEntitiesToBiomes( BiomeLoadingEvent event ) {
		Biome.Category category = event.getCategory();
		MobSpawnInfoBuilder spawnInfoBuilder = event.getSpawns();

		if( doBiomeBelongsToOverworld( category ) )
			addOverworldEntities( spawnInfoBuilder );
		else if( doBiomeBelongsToNether( category ) )
			addNetherEntities( spawnInfoBuilder );
	}

	protected static void addOverworldEntities( MobSpawnInfoBuilder spawnInfoBuilder ) {
		spawnInfoBuilder.withSpawner( EntityClassification.MONSTER, new MobSpawnInfo.Spawners( EntityType.ILLUSIONER, 20, 1, 2 ) );
		spawnInfoBuilder.withSpawner( EntityClassification.MONSTER, new MobSpawnInfo.Spawners( GiantEntity.type, 3, 1, 1 ) );
		spawnInfoBuilder.withSpawner( EntityClassification.CREATURE, new MobSpawnInfo.Spawners( PillagerWolfEntity.type, 5, 2, 5 ) );
		spawnInfoBuilder.withSpawner( EntityClassification.MONSTER, new MobSpawnInfo.Spawners( EliteSkeletonEntity.type, 20, 1, 1 ) );
	}

	protected static boolean doBiomeBelongsToOverworld( Biome.Category category ) {
		boolean isNotNone = category != Biome.Category.NONE;
		boolean isNotNether = category != Biome.Category.NETHER;
		boolean isNotEnd = category != Biome.Category.THEEND;
		boolean isNotOcean = category != Biome.Category.OCEAN;

		return isNotNone && isNotNether && isNotEnd && isNotOcean;
	}

	protected static void addNetherEntities( MobSpawnInfoBuilder spawnInfoBuilder ) {
		// spawnInfoBuilder.func_242575_a( EntityClassification.MONSTER, new MobSpawnInfo.Spawners( EliteSkeletonEntity.type, 100, 1, 1 ) );
	}

	protected static boolean doBiomeBelongsToNether( Biome.Category category ) {
		return category == Biome.Category.NETHER;
	}
}
