package com.majruszs_difficulty.structures;

import com.google.common.collect.ImmutableMap;
import com.majruszs_difficulty.Instances;
import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.IntegerConfig;
import com.mojang.serialization.Codec;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;

import static com.majruszs_difficulty.MajruszsDifficulty.STRUCTURES_GROUP;

public abstract class NoFeatureBaseStructure extends Structure< NoFeatureConfig > {
	protected StructureSeparationSettings separationSettings;
	protected final ConfigGroup group;
	protected final AvailabilityConfig availability;
	protected final IntegerConfig minimumDistance;
	protected final IntegerConfig maximumDistance;
	protected final int structureID;
	protected final StructureFeature< NoFeatureConfig, ? extends Structure< NoFeatureConfig > > structureFeature;

	public NoFeatureBaseStructure( String configName, String commentStructureName, int structureID, StructureFeature< NoFeatureConfig, ? extends Structure< NoFeatureConfig > > structureFeature ) {
		super( NoFeatureConfig.field_236558_a_ );

		String availability_comment = "Is this structure enabled?";
		String min_comment = "Minimum distance in chunks between this structures.";
		String max_comment = "Maximum distance in chunks between this structures.";
		String group_comment = "Configuration for " + commentStructureName + " structure.";
		this.availability = new AvailabilityConfig( "is_enabled", availability_comment, true, true );
		this.minimumDistance = new IntegerConfig( "minimum_distance", min_comment, true, 47, 10, 200 );
		this.maximumDistance = new IntegerConfig( "maximum_distance", max_comment, true, 67, 10, 200 );
		this.group = STRUCTURES_GROUP.addGroup( new ConfigGroup( configName, group_comment ) );
		this.group.addConfigs( this.availability, this.minimumDistance, this.maximumDistance );
		this.structureID = structureID;
		this.structureFeature = structureFeature;
	}

	protected boolean func_230363_a_( ChunkGenerator p_230363_1_, BiomeProvider p_230363_2_, long p_230363_3_, SharedSeedRandom p_230363_5_, int p_230363_6_, int p_230363_7_, Biome p_230363_8_, ChunkPos p_230363_9_, NoFeatureConfig p_230363_10_ ) {
		return this.availability.isEnabled();
	}

	public void setup() {
		int minimum = this.minimumDistance.get();
		int maximum = Math.max( this.maximumDistance.get(), minimum + 1 );

		separationSettings = new StructureSeparationSettings( maximum, minimum, this.structureID );
		DimensionStructuresSettings.field_236191_b_ = ImmutableMap.< Structure< ? >, StructureSeparationSettings > builder().putAll(
			DimensionStructuresSettings.field_236191_b_ )
			.put( this, this.separationSettings )
			.build();
		DimensionSettings.field_242740_q.getStructures().field_236193_d_.put( this, this.separationSettings );
		FlatGenerationSettings.STRUCTURES.put( this, this.structureFeature );
	}
}
