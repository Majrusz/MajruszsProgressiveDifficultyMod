package com.majruszs_difficulty.structures;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.entities.SkyKeeperEntity;
import com.majruszs_difficulty.structure_pieces.FlyingPhantomPiece;
import com.mlib.MajruszLibrary;
import com.mlib.config.*;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;

import java.util.List;

import static com.majruszs_difficulty.MajruszsDifficulty.STRUCTURES_GROUP;

public class FlyingPhantomStructure extends Structure< NoFeatureConfig > {
	protected StructureSeparationSettings separationSettings;
	protected final ConfigGroup group;
	protected final AvailabilityConfig availability;
	protected final IntegerConfig minimumDistance;
	protected final IntegerConfig maximumDistance;

	private static final List< MobSpawnInfo.Spawners > STRUCTURE_MONSTERS = ImmutableList.of(
		new MobSpawnInfo.Spawners( SkyKeeperEntity.type, 40, 1, 1 ), new MobSpawnInfo.Spawners( EntityType.PHANTOM, 10, 1, 1 ) );

	public FlyingPhantomStructure() {
		super( NoFeatureConfig.field_236558_a_ );

		String availability_comment = "Is this structure enabled?";
		String min_comment = "Minimum distance in chunks between this structures.";
		String max_comment = "Maximum distance in chunks between this structures.";
		String group_comment = "Configuration for Flying Phantom structure.";
		this.availability = new AvailabilityConfig( "is_enabled", availability_comment, true, true );
		this.minimumDistance = new IntegerConfig( "minimum_distance", min_comment, true, 47, 10, 200 );
		this.maximumDistance = new IntegerConfig( "maximum_distance", max_comment, true, 67, 10, 200 );
		this.group = STRUCTURES_GROUP.addGroup( new ConfigGroup( "FlyingPhantom", group_comment ) );
		this.group.addConfigs( this.availability, this.minimumDistance, this.maximumDistance );
	}

	/** Generation stage for where to generate the structure. */
	@Override
	public GenerationStage.Decoration getDecorationStage() {
		return GenerationStage.Decoration.RAW_GENERATION;
	}

	public Structure.IStartFactory< NoFeatureConfig > getStartFactory() {
		return FlyingPhantomStructure.Start::new;
	}

	protected boolean func_230363_a_( ChunkGenerator p_230363_1_, BiomeProvider p_230363_2_, long p_230363_3_, SharedSeedRandom p_230363_5_, int p_230363_6_, int p_230363_7_, Biome p_230363_8_, ChunkPos p_230363_9_, NoFeatureConfig p_230363_10_ ) {
		return this.availability.isEnabled();
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
			int y = Math.min( 60, chunkGenerator.getHeight( x, z, Heightmap.Type.WORLD_SURFACE_WG ) ) + 60 + MajruszLibrary.RANDOM.nextInt( 60 );

			BlockPos blockpos = new BlockPos( x, y, z );

			FlyingPhantomPiece.start( templateManager, blockpos, rotation, this.components, this.rand );

			this.recalculateStructureSize();
		}
	}

	public void setup() {
		int minimum = this.minimumDistance.get();
		int maximum = Math.max( this.maximumDistance.get(), minimum + 1 );

		separationSettings = new StructureSeparationSettings( maximum, minimum, 1717171717 );
		DimensionStructuresSettings.field_236191_b_ = ImmutableMap.< Structure< ? >, StructureSeparationSettings > builder().putAll(
			DimensionStructuresSettings.field_236191_b_ )
			.put( this, this.separationSettings )
			.build();
		DimensionSettings.field_242740_q.getStructures().field_236193_d_.put( this, this.separationSettings );
		FlatGenerationSettings.STRUCTURES.put( this, Instances.FLYING_PHANTOM_FEATURE );
	}
}