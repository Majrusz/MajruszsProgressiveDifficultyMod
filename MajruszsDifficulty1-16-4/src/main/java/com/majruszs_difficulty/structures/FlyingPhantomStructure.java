package com.majruszs_difficulty.structures;

import com.google.common.collect.ImmutableList;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.RegistryHandler;
import com.majruszs_difficulty.entities.SkyKeeperEntity;
import com.majruszs_difficulty.structure_pieces.FlyingPhantomPiece;
import com.mojang.serialization.Codec;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.settings.StructureSeparationSettings;

import java.util.List;

public class FlyingPhantomStructure extends Structure< NoFeatureConfig > {
	public static final FlyingPhantomStructure INSTANCE = new FlyingPhantomStructure();
	public static final String NAME = MajruszsHelper.getResource( "flying_phantom_structure" ).toString();
	public static final StructureFeature< NoFeatureConfig, ? extends Structure< NoFeatureConfig > > FEATURE =
		WorldGenRegistries.register( WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, NAME, INSTANCE.withConfiguration( NoFeatureConfig.field_236559_b_ ) );
	public static final StructureSeparationSettings SEPARATION_SETTINGS = new StructureSeparationSettings(37, 27, 1717171717 );

	private static final List< MobSpawnInfo.Spawners > STRUCTURE_MONSTERS = ImmutableList.of(
		new MobSpawnInfo.Spawners( SkyKeeperEntity.type, 1, 3, 4 )
	);

	public FlyingPhantomStructure() {
		super( NoFeatureConfig.field_236558_a_ );
	}

	public Structure.IStartFactory< NoFeatureConfig > getStartFactory() {
		return FlyingPhantomStructure.Start::new;
	}

	/** Generation stage for where to generate the structure. */
	@Override
	public GenerationStage.Decoration getDecorationStage() {
		return GenerationStage.Decoration.SURFACE_STRUCTURES;
	}

	/** Enemies spawning naturally near the structure. */
	@Override
	public List< MobSpawnInfo.Spawners > getDefaultSpawnList() {
		return STRUCTURE_MONSTERS;
	}

	public static class Start extends StructureStart< NoFeatureConfig > {
		public Start( Structure< NoFeatureConfig > structure, int chunkX, int chunkZ, MutableBoundingBox mutableBoundingBox, int reference, long seed ) {
			super( structure, chunkX, chunkZ, mutableBoundingBox, reference, seed );
		}

		@Override
		public void func_230364_a_( DynamicRegistries dynamicRegistryManager, ChunkGenerator chunkGenerator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, NoFeatureConfig config) {
			Rotation rotation = Rotation.values()[this.rand.nextInt(Rotation.values().length)];

			// Turns the chunk coordinates into actual coordinates we can use. (Gets center of that chunk)
			int x = (chunkX << 4) + 7;
			int z = (chunkZ << 4) + 7;

			// Finds the y value of the terrain at location.
			int surfaceY = chunkGenerator.getHeight(x, z, Heightmap.Type.WORLD_SURFACE_WG);
			BlockPos blockpos = new BlockPos(x, surfaceY + 100, z);

			// Now adds the structure pieces to this.components with all details such as where each part goes
			// so that the structure can be added to the world by worldgen.
			FlyingPhantomPiece.start( templateManagerIn, blockpos, rotation, this.components, this.rand );

			// Sets the bounds of the structure.
			this.recalculateStructureSize();

			// I use to debug and quickly find out if the structure is spawning or not and where it is.
			MajruszsDifficulty.LOGGER.info( "Rundown House at " + (blockpos.getX()) + " " + blockpos.getY() + " " + (blockpos.getZ()));
		}
	}
}