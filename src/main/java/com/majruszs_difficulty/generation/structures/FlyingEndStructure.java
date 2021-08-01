package com.majruszs_difficulty.generation.structures;

/** Flying End structures in The End. */
public abstract class FlyingEndStructure {}
/*extends NoFeatureBaseStructure {
	private static final List< MobSpawnInfo.Spawners > STRUCTURE_MONSTERS = ImmutableList.of(
		new MobSpawnInfo.Spawners( SkyKeeperEntity.type, 10, 1, 1 ) );

	public FlyingEndStructure( String configName, String commentStructureName, int structureID, int minimumDistance, int maximumDistance,
		StructureFeature< NoFeatureConfig, ? extends Structure< NoFeatureConfig > > structureFeature
	) {
		super( configName, commentStructureName, structureID, minimumDistance, maximumDistance, structureFeature );
	}

	// /** Generation stage for where to generate the structure. /
	@Override
	public GenerationStage.Decoration getDecorationStage() {
		return GenerationStage.Decoration.RAW_GENERATION;
	}

	// /** Enemies spawning naturally near the structure. /
	@Override
	public List< MobSpawnInfo.Spawners > getDefaultSpawnList() {
		return STRUCTURE_MONSTERS;
	}

	// /** Checking whether structure can spawn at given position. /
	@Override
	protected boolean func_230363_a_( ChunkGenerator chunkGenerator, BiomeProvider biomeProvider, long p_230363_3_, SharedSeedRandom sharedSeedRandom,
		int chunkX, int chunkZ, Biome biome, ChunkPos chunkPosition, NoFeatureConfig noFeatureConfig
	) {
		int x = ( chunkX << 4 ) + 7, z = ( chunkZ << 4 ) + 7;
		int y = chunkGenerator.getHeight( x, z, Heightmap.Types.WORLD_SURFACE_WG );

		return y < 20 && super.func_230363_a_( chunkGenerator, biomeProvider, p_230363_3_, sharedSeedRandom, chunkX, chunkZ, biome, chunkPosition,
			noFeatureConfig
		);
	}
} */