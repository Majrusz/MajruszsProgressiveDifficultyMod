package com.majruszsdifficulty.world;

import com.majruszsdifficulty.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class BiomeLoader {
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

	static void addOverworldEntities( MobSpawnSettingsBuilder spawnInfoBuilder ) {
		addFreshEntity( spawnInfoBuilder, MobCategory.MONSTER, EntityType.ILLUSIONER, 20, 1, 2 );
		addFreshEntity( spawnInfoBuilder, MobCategory.MONSTER, Registries.TANK.get(), 3, 1, 1 );
	}

	static void addOverworldStructures( BiomeGenerationSettingsBuilder generationSettingsBuilder ) {}

	static void addNetherEntities( MobSpawnSettingsBuilder spawnInfoBuilder ) {}

	protected static void addEndEntities( MobSpawnSettingsBuilder spawnInfoBuilder ) {}

	protected static void addEndStructures( BiomeGenerationSettingsBuilder generationSettingsBuilder ) {}

	protected static void addEndOres( BiomeGenerationSettingsBuilder generationSettingsBuilder ) {
		generationSettingsBuilder.addFeature( GenerationStep.Decoration.UNDERGROUND_ORES, Registries.INFESTED_END_STONE_PLACED.getHolder().get() );
		generationSettingsBuilder.addFeature( GenerationStep.Decoration.UNDERGROUND_ORES, Registries.ENDERIUM_ORE_SMALL_PLACED.getHolder().get() );
		generationSettingsBuilder.addFeature( GenerationStep.Decoration.UNDERGROUND_ORES, Registries.ENDERIUM_ORE_LARGE_PLACED.getHolder().get() );
	}

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

	protected static boolean doBiomeCategoryBelongsToNether( Biome.BiomeCategory category ) {
		return category == Biome.BiomeCategory.NETHER;
	}

	protected static boolean doBiomeCategoryBelongsToTheEnd( Biome.BiomeCategory category ) {
		return category == Biome.BiomeCategory.THEEND;
	}

	private static void addFreshEntity( MobSpawnSettingsBuilder spawnInfoBuilder, MobCategory classification, EntityType< ? > entityType, int weight,
		int minimumCount, int maximumCount
	) {
		MobSpawnSettings.SpawnerData spawners = new MobSpawnSettings.SpawnerData( entityType, weight, minimumCount, maximumCount );

		spawnInfoBuilder.addSpawn( classification, spawners );
	}
}