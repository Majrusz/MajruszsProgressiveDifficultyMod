package com.majruszs_difficulty.structures;

import com.google.common.collect.ImmutableList;
import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.entities.SkyKeeperEntity;
import com.majruszs_difficulty.structure_pieces.FlyingEndIslandPiece;
import com.majruszs_difficulty.structure_pieces.FlyingPhantomPiece;
import com.mlib.MajruszLibrary;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;

public class FlyingEndIslandStructure extends NoFeatureBaseStructure {
	private static final List< MobSpawnInfo.Spawners > STRUCTURE_MONSTERS = ImmutableList.of( new MobSpawnInfo.Spawners( SkyKeeperEntity.type, 10, 1, 1 ) );

	public FlyingEndIslandStructure() {
		super( "FlyingEndIsland", "Flying End Island", 1717171718, 20, 60, Instances.FLYING_END_ISLAND_FEATURE );
	}

	/** Generation stage for where to generate the structure. */
	@Override
	public GenerationStage.Decoration getDecorationStage() {
		return GenerationStage.Decoration.RAW_GENERATION;
	}

	/** Factory for generating new structures. */
	public IStartFactory< NoFeatureConfig > getStartFactory() {
		return FlyingEndIslandStructure.Start::new;
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

			int x = ( chunkX << 4 ) + 7, z = ( chunkZ << 4 ) + 7; // Turns the chunk coordinates into actual coordinates we can use. (Gets center of that chunk)
			int y = Math.min( 60, chunkGenerator.getHeight( x, z, Heightmap.Type.WORLD_SURFACE_WG ) ) + 60 + MajruszLibrary.RANDOM.nextInt( 60 );

			BlockPos blockpos = new BlockPos( x, y, z );
			FlyingEndIslandPiece.start( templateManager, blockpos, rotation, this.components, this.rand );

			this.recalculateStructureSize();
		}
	}
}