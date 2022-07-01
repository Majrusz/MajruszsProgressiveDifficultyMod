package com.majruszsdifficulty.world;

import com.majruszsdifficulty.Registries;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public record EntityBiomeModifier( HolderSet< Biome > biomes, MobSpawnSettings.SpawnerData spawnerData ) implements BiomeModifier {
	@Override
	public void modify( Holder< Biome > biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder ) {
		if( phase == Phase.ADD && this.biomes.contains( biome ) ) {
			builder.getMobSpawnSettings().addSpawn( MobCategory.MONSTER, this.spawnerData );
		}
	}

	@Override
	public Codec< ? extends BiomeModifier > codec() {
		return Registries.ENTITY_MODIFIER.get();
	}
}
