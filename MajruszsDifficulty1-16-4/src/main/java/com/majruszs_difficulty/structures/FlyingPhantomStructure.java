package com.majruszs_difficulty.structures;

import com.google.common.collect.ImmutableList;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.entities.SkyKeeperEntity;
import com.majruszs_difficulty.structure_pieces.FlyingPhantomPiece;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.settings.StructureSeparationSettings;

import java.util.List;

public class FlyingPhantomStructure extends Structure< NoFeatureConfig > {
	public static final FlyingPhantomStructure INSTANCE = new FlyingPhantomStructure();
	public static final String NAME = MajruszsHelper.getResource( "flying_phantom_structure" )
		.toString();
	public static final StructureFeature< NoFeatureConfig, ? extends Structure< NoFeatureConfig > > FEATURE = WorldGenRegistries.register(
		WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, NAME, INSTANCE.withConfiguration( NoFeatureConfig.field_236559_b_ ) );
	public static StructureSeparationSettings SEPARATION_SETTINGS;

	private static final List< MobSpawnInfo.Spawners > STRUCTURE_MONSTERS = ImmutableList.of(
		new MobSpawnInfo.Spawners( SkyKeeperEntity.type, 40, 1, 1 ), new MobSpawnInfo.Spawners( EntityType.PHANTOM, 10, 1, 1 ) );

	public FlyingPhantomStructure() {
		super( NoFeatureConfig.field_236558_a_ );
	}

	/** Generation stage for where to generate the structure. */
	@Override
	public GenerationStage.Decoration getDecorationStage() {
		return GenerationStage.Decoration.SURFACE_STRUCTURES;
	}

	public Structure.IStartFactory< NoFeatureConfig > getStartFactory() {
		return FlyingPhantomStructure.Start::new;
	}

	/** Enemies spawning naturally near the structure. */
	@Override
	public List< MobSpawnInfo.Spawners > getDefaultSpawnList() {
		return STRUCTURE_MONSTERS;
	}

	public static class Start extends StructureStart< NoFeatureConfig > {
		public Start( Structure< NoFeatureConfig > structure, int chunkX, int chunkZ, MutableBoundingBox mutableBoundingBox, int reference, long seed
		) {
			super( structure, chunkX, chunkZ, mutableBoundingBox, reference, seed );
		}

		@Override
		public void func_230364_a_( DynamicRegistries dynamicRegistryManager, ChunkGenerator chunkGenerator, TemplateManager templateManager,
			int chunkX, int chunkZ, Biome biome, NoFeatureConfig config
		) {
			Rotation rotation = Rotation.values()[ this.rand.nextInt( Rotation.values().length ) ];

			// Turns the chunk coordinates into actual coordinates we can use. (Gets center of that chunk)
			int x = ( chunkX << 4 ) + 7;
			int z = ( chunkZ << 4 ) + 7;
			int y = chunkGenerator.getHeight( x, z, Heightmap.Type.WORLD_SURFACE_WG ) + 60 + MajruszsDifficulty.RANDOM.nextInt( 60 );

			BlockPos blockpos = new BlockPos( x, y, z );

			FlyingPhantomPiece.start( templateManager, blockpos, rotation, this.components, this.rand );

			this.recalculateStructureSize();
		}
	}

	public static void setupSeparationSettings() {
		int minimum = 47; // TODO
		int maximum = Math.max( 67, minimum + 1 );
		MajruszsDifficulty.LOGGER.info( minimum + ":" + maximum );
		SEPARATION_SETTINGS = new StructureSeparationSettings( maximum, minimum, 1717171717 );
	}
}