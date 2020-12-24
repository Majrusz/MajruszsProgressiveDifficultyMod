package com.majruszs_difficulty.events;

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

		if( category != Biome.Category.NETHER && category != Biome.Category.THEEND && category != Biome.Category.NONE && category != Biome.Category.OCEAN ) {
			MobSpawnInfoBuilder spawnInfoBuilder = event.getSpawns();

			spawnInfoBuilder.func_242575_a( EntityClassification.MONSTER, new MobSpawnInfo.Spawners( EntityType.ILLUSIONER, 25, 1, 2 ) );
			spawnInfoBuilder.func_242575_a( EntityClassification.MONSTER, new MobSpawnInfo.Spawners( GiantEntity.type, 3, 1, 1 ) );
			spawnInfoBuilder.func_242575_a( EntityClassification.CREATURE, new MobSpawnInfo.Spawners( PillagerWolfEntity.type, 5, 2, 5 ) );
		}
	}
}
