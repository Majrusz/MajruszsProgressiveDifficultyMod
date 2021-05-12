package com.majruszs_difficulty.generation.structures;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.generation.structure_pieces.FlyingEndShipPiece;
import com.mlib.MajruszLibrary;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

/** Flying Island structures in The End. */
public class FlyingEndShipStructure extends FlyingEndStructure {
	public FlyingEndShipStructure() {
		super( "FlyingEndShip", "Flying End Ship", 1717171719, 32, 64, Instances.FLYING_END_SHIP_FEATURE );
	}

	/** Factory for generating new structures. */
	public IStartFactory< NoFeatureConfig > getStartFactory() {
		return FlyingEndShipStructure.Start::new;
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
			int y = Math.max( chunkGenerator.getHeight( x, z, Heightmap.Type.WORLD_SURFACE_WG ) + 6, 40 + MajruszLibrary.RANDOM.nextInt( 30 ) );

			BlockPos blockpos = new BlockPos( x, y, z );
			FlyingEndShipPiece.start( templateManager, blockpos, rotation, this.components, this.rand );

			this.recalculateStructureSize();
		}
	}
}