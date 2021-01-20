package com.majruszs_difficulty.events;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.entities.EliteSkeletonEntity;
import com.majruszs_difficulty.entities.GiantEntity;
import com.majruszs_difficulty.entities.PillagerWolfEntity;
import com.majruszs_difficulty.structures.FlyingPhantomStructure;
import com.mlib.MajruszLibrary;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Adding natural spawn for entities and natural generation for structures. */
@Mod.EventBusSubscriber
public class BiomeEntityAndStructureLoading {
	@SubscribeEvent( priority = EventPriority.HIGH )
	public static void addEntitiesAndStructuresToBiomes( BiomeLoadingEvent event ) {
		Biome.Category category = event.getCategory();
		MobSpawnInfoBuilder spawnInfoBuilder = event.getSpawns();
		BiomeGenerationSettingsBuilder generationSettingsBuilder = event.getGeneration();

		if( doBiomeCategoryBelongsToOverworld( category ) ) {
			addOverworldEntities( spawnInfoBuilder );
			addOverworldStructures( generationSettingsBuilder );
		} else if( doBiomeCategoryBelongsToNether( category ) ) {
			addNetherEntities( spawnInfoBuilder );
		} else if( doBiomeCategoryBelongsToTheEnd( category ) ) {
			addEndStructures( generationSettingsBuilder );
			MajruszLibrary.LOGGER.debug( generationSettingsBuilder.toString() );
		}
	}

	/** Adding natural spawning for overworld entities. */
	protected static void addOverworldEntities( MobSpawnInfoBuilder spawnInfoBuilder ) {
		addEntity( spawnInfoBuilder, EntityClassification.MONSTER, EntityType.ILLUSIONER, 20, 1, 2 );
		addEntity( spawnInfoBuilder, EntityClassification.MONSTER, GiantEntity.type, 3, 1, 1 );
		addEntity( spawnInfoBuilder, EntityClassification.CREATURE, PillagerWolfEntity.type, 1, 1, 4 );
		addEntity( spawnInfoBuilder, EntityClassification.MONSTER, EliteSkeletonEntity.type, 20, 1, 1 );
	}

	/** Adding natural generating for overworld structures. */
	protected static void addOverworldStructures( BiomeGenerationSettingsBuilder generationSettingsBuilder ) {
		// generationSettingsBuilder.withStructure( Instances.FLYING_PHANTOM_FEATURE );
	}

	/** Adding natural spawning for nether entities. */
	protected static void addNetherEntities( MobSpawnInfoBuilder spawnInfoBuilder ) {
		addEntity( spawnInfoBuilder, EntityClassification.MONSTER, EliteSkeletonEntity.type, 10, 1, 1 );
	}

	/** Adding natural generating for end structures. */
	protected static void addEndStructures( BiomeGenerationSettingsBuilder generationSettingsBuilder ) {
		generationSettingsBuilder.withStructure( Instances.FLYING_PHANTOM_FEATURE );
	}

	/** Checking whether given biome category belongs to overworld. */
	protected static boolean doBiomeCategoryBelongsToOverworld( Biome.Category category ) {
		boolean isTaiga = category == Biome.Category.TAIGA;
		boolean isExtremeHills = category == Biome.Category.EXTREME_HILLS;
		boolean isJungle = category == Biome.Category.JUNGLE;
		boolean isMesa = category == Biome.Category.MESA;
		boolean isPlains = category == Biome.Category.PLAINS;
		boolean isSavanna = category == Biome.Category.SAVANNA;
		boolean isIcy = category == Biome.Category.ICY;
		boolean isBeach = category == Biome.Category.BEACH;
		boolean isForest = category == Biome.Category.FOREST;
		boolean isOcean = category == Biome.Category.OCEAN;
		boolean isDesert = category == Biome.Category.DESERT;
		boolean isRiver = category == Biome.Category.RIVER;
		boolean isSwamp = category == Biome.Category.SWAMP;
		boolean isMushroom = category == Biome.Category.MUSHROOM;

		return isTaiga || isExtremeHills || isJungle || isMesa || isPlains || isSavanna || isIcy || isBeach || isForest || isOcean || isDesert || isRiver || isSwamp || isMushroom;
	}

	/** Checking whether given biome category belongs to the nether. */
	protected static boolean doBiomeCategoryBelongsToNether( Biome.Category category ) {
		return category == Biome.Category.NETHER;
	}

	/** Checking whether given biome category belongs to the end. */
	protected static boolean doBiomeCategoryBelongsToTheEnd( Biome.Category category ) {
		return category == Biome.Category.THEEND;
	}

	/**
	 Adding entity to current spawn info builder.

	 @param spawnInfoBuilder Builder where entity should be added.
	 @param classification   Classification of the entity. (creature, monster, etc.)
	 @param entityType       Type of entity.
	 @param weight           Weight of spawning. (for example skeleton has 100 weight)
	 @param minimumCount     Minimum amount of entities to spawn.
	 @param maximumCount     Maximum amount of entities to spawn.
	 */
	private static void addEntity( MobSpawnInfoBuilder spawnInfoBuilder, EntityClassification classification, EntityType< ? > entityType, int weight,
		int minimumCount, int maximumCount
	) {
		MobSpawnInfo.Spawners spawners = new MobSpawnInfo.Spawners( entityType, weight, minimumCount, maximumCount );

		spawnInfoBuilder.withSpawner( classification, spawners );
	}
}
