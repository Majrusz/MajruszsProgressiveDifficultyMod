package com.majruszs_difficulty.features;

import com.majruszs_difficulty.entities.EliteSkeletonEntity;
import com.majruszs_difficulty.entities.GiantEntity;
import com.majruszs_difficulty.entities.ParasiteEntity;
import com.majruszs_difficulty.entities.TankEntity;
import com.majruszs_difficulty.generation.OreGeneration;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

/** Adding natural spawn for entities, natural ore generation and natural generation for structures. */
@Mod.EventBusSubscriber
public class BiomeLoading {
	@SubscribeEvent( priority = EventPriority.HIGH )
	public static void onLoad( BiomeLoadingEvent event ) {
		Biome.BiomeCategory category = event.getCategory();
		MobSpawnSettingsBuilder spawnInfoBuilder = event.getSpawns();
		BiomeGenerationSettingsBuilder generationSettingsBuilder = event.getGeneration();

		if( doBiomeCategoryBelongsToOverworld( category ) ) {
			addOverworldEntities( spawnInfoBuilder );
			addOverworldStructures( generationSettingsBuilder );
		} else if( doBiomeCategoryBelongsToNether( category ) ) {
			addNetherEntities( spawnInfoBuilder );
		} else if( doBiomeCategoryBelongsToTheEnd( category ) ) {
			addEndEntities( spawnInfoBuilder );
			addEndStructures( generationSettingsBuilder );
			addEndOres( generationSettingsBuilder );
		}
	}

	/** Adding natural spawning for overworld entities. */
	protected static void addOverworldEntities( MobSpawnSettingsBuilder spawnInfoBuilder ) {
		addFreshEntity( spawnInfoBuilder, MobCategory.MONSTER, EntityType.ILLUSIONER, 20, 1, 2 );
		addFreshEntity( spawnInfoBuilder, MobCategory.MONSTER, GiantEntity.type, 3, 1, 1 );
		addFreshEntity( spawnInfoBuilder, MobCategory.MONSTER, EliteSkeletonEntity.type, 20, 1, 1 );
		addFreshEntity( spawnInfoBuilder, MobCategory.MONSTER, TankEntity.type, 3, 1, 1 );
	}

	/** Adding natural generating for overworld structures. */
	protected static void addOverworldStructures( BiomeGenerationSettingsBuilder generationSettingsBuilder ) {
		// generationSettingsBuilder.withStructure( Instances.FLYING_PHANTOM_FEATURE );
	}

	/** Adding natural spawning for nether entities. */
	protected static void addNetherEntities( MobSpawnSettingsBuilder spawnInfoBuilder ) {
		Set< EntityType< ? > > entityTypes = spawnInfoBuilder.getEntityTypes();
		if( entityTypes.contains( EntityType.SKELETON ) || entityTypes.contains( EntityType.WITHER_SKELETON ) )
			addFreshEntity( spawnInfoBuilder, MobCategory.MONSTER, EliteSkeletonEntity.type, 5, 1, 1 );
	}

	/** Adding natural spawning for end entities. */
	protected static void addEndEntities( MobSpawnSettingsBuilder spawnInfoBuilder ) {
		addFreshEntity( spawnInfoBuilder, MobCategory.MONSTER, ParasiteEntity.type, 1, 2, 6 );
	}

	/** Adding natural generating for end structures. */
	protected static void addEndStructures( BiomeGenerationSettingsBuilder generationSettingsBuilder ) {
		// generationSettingsBuilder.addStructureStart( Instances.FLYING_PHANTOM_FEATURE );
		// generationSettingsBuilder.addStructureStart( Instances.FLYING_END_ISLAND_FEATURE );
		// generationSettingsBuilder.addStructureStart( Instances.FLYING_END_SHIP_FEATURE );
	}

	/** Adding natural generation for ores. */
	protected static void addEndOres( BiomeGenerationSettingsBuilder generationSettingsBuilder ) {
		for( PlacedFeature feature : OreGeneration.END_ORES )
			generationSettingsBuilder.addFeature( GenerationStep.Decoration.UNDERGROUND_ORES, feature );
	}

	/** Checking whether given biome category belongs to overworld. */
	protected static boolean doBiomeCategoryBelongsToOverworld( Biome.BiomeCategory category ) {
		boolean isTaiga = category == Biome.BiomeCategory.TAIGA;
		boolean isExtremeHills = category == Biome.BiomeCategory.EXTREME_HILLS;
		boolean isJungle = category == Biome.BiomeCategory.JUNGLE;
		boolean isMesa = category == Biome.BiomeCategory.MESA;
		boolean isPlains = category == Biome.BiomeCategory.PLAINS;
		boolean isSavanna = category == Biome.BiomeCategory.SAVANNA;
		boolean isIcy = category == Biome.BiomeCategory.ICY;
		boolean isBeach = category == Biome.BiomeCategory.BEACH;
		boolean isForest = category == Biome.BiomeCategory.FOREST;
		boolean isOcean = category == Biome.BiomeCategory.OCEAN;
		boolean isDesert = category == Biome.BiomeCategory.DESERT;
		boolean isRiver = category == Biome.BiomeCategory.RIVER;
		boolean isSwamp = category == Biome.BiomeCategory.SWAMP;
		boolean isMushroom = category == Biome.BiomeCategory.MUSHROOM;

		return isTaiga || isExtremeHills || isJungle || isMesa || isPlains || isSavanna || isIcy || isBeach || isForest || isOcean || isDesert || isRiver || isSwamp || isMushroom;
	}

	/** Checking whether given biome category belongs to the nether. */
	protected static boolean doBiomeCategoryBelongsToNether( Biome.BiomeCategory category ) {
		return category == Biome.BiomeCategory.NETHER;
	}

	/** Checking whether given biome category belongs to the end. */
	protected static boolean doBiomeCategoryBelongsToTheEnd( Biome.BiomeCategory category ) {
		return category == Biome.BiomeCategory.THEEND;
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
	private static void addFreshEntity( MobSpawnSettingsBuilder spawnInfoBuilder, MobCategory classification, EntityType< ? > entityType, int weight,
		int minimumCount, int maximumCount
	) {
		MobSpawnSettings.SpawnerData spawners = new MobSpawnSettings.SpawnerData( entityType, weight, minimumCount, maximumCount );

		spawnInfoBuilder.addSpawn( classification, spawners );
	}
}
