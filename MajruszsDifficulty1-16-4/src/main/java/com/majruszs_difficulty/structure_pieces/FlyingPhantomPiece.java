package com.majruszs_difficulty.structure_pieces;

import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.RegistryHandler;
import com.majruszs_difficulty.entities.SkyKeeperEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.Random;

public class FlyingPhantomPiece extends TemplateStructurePiece {
	public static final ResourceLocation resourceLocation = MajruszsHelper.getResource( "flying_phantom" );
	public static final ResourceLocation chestResourceLocation = MajruszsHelper.getResource( "chests/flying_phantom" );
	private final Rotation rotation;

	public FlyingPhantomPiece( TemplateManager templateManager, BlockPos position, Rotation rotation ) {
		super( RegistryHandler.FLYING_PHANTOM_PIECE, 0 );
		this.templatePosition = position;
		this.rotation = rotation;
		this.setupPiece( templateManager );
	}

	public FlyingPhantomPiece( TemplateManager templateManager, CompoundNBT compoundNBT ) {
		super( RegistryHandler.FLYING_PHANTOM_PIECE, compoundNBT );
		this.rotation = Rotation.valueOf( compoundNBT.getString( "Rot" ) );
		this.setupPiece( templateManager );
	}

	@Override
	protected void readAdditional( CompoundNBT compoundNBT ) {
		super.readAdditional( compoundNBT );

		compoundNBT.putString( "Rot", this.rotation.name() );
	}

	@Override
	protected void handleDataMarker( String function, BlockPos position, IServerWorld world, Random random, MutableBoundingBox boundingBox ) {
		if( function.startsWith( "chest" ) ) {
			world.setBlockState( position, Blocks.AIR.getDefaultState(), 2 );
			TileEntity tileEntity = world.getTileEntity( position.down() );

			if( tileEntity instanceof ChestTileEntity )
				( ( ChestTileEntity )tileEntity ).setLootTable( chestResourceLocation, random.nextLong() );

		} else if( function.startsWith( "spawner" ) ) {
			world.setBlockState( position, Blocks.AIR.getDefaultState(), 2 );
			TileEntity tileEntity = world.getTileEntity( position.down() );

			if( tileEntity instanceof MobSpawnerTileEntity ) {
				EntityType< ? > entityType = function.equals( "spawner_keeper" ) ? SkyKeeperEntity.type : EntityType.PHANTOM;

				( ( MobSpawnerTileEntity )tileEntity ).getSpawnerBaseLogic()
					.setEntityType( entityType );
			}
		}
	}

	/** Begins assembling your structure and where the pieces needs to go. */
	public static void start( TemplateManager templateManager, BlockPos position, Rotation rotation, List< StructurePiece > pieces, Random random ) {
		BlockPos rotationOffSet = new BlockPos( 0, 0, 0 ).rotate( rotation );
		BlockPos blockpos = rotationOffSet.add( position.getX(), position.getY(), position.getZ() );

		pieces.add( new FlyingPhantomPiece( templateManager, blockpos, rotation ) );
	}

	private void setupPiece( TemplateManager templateManager ) {
		Template template = templateManager.getTemplateDefaulted( resourceLocation );
		PlacementSettings placementsettings = ( new PlacementSettings() ).setRotation( this.rotation )
			.setMirror( Mirror.NONE );

		this.setup( template, this.templatePosition, placementsettings );
	}
}