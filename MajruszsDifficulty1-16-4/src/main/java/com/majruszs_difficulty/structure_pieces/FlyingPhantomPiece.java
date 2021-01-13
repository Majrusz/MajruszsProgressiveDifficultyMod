package com.majruszs_difficulty.structure_pieces;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.RegistryHandler;
import com.majruszs_difficulty.entities.SkyKeeperEntity;
import com.majruszs_difficulty.structures.FlyingPhantomStructure;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
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
	private final Rotation rotation;

	public FlyingPhantomPiece( TemplateManager templateManagerIn, BlockPos pos, Rotation rotationIn ) {
		super( RegistryHandler.FLYING_PHANTOM_PIECE, 0 );
		this.templatePosition = pos;
		this.rotation = rotationIn;
		this.setupPiece( templateManagerIn );
	}

	public FlyingPhantomPiece( TemplateManager templateManagerIn, CompoundNBT tagCompound ) {
		super( RegistryHandler.FLYING_PHANTOM_PIECE, tagCompound );
		this.rotation = Rotation.valueOf( tagCompound.getString( "Rot" ) );
		this.setupPiece( templateManagerIn );
	}

	@Override
	protected void readAdditional( CompoundNBT tagCompound ) {
		super.readAdditional( tagCompound );
		tagCompound.putString( "Rot", this.rotation.name() );
	}

	@Override
	protected void handleDataMarker( String function, BlockPos pos, IServerWorld worldIn, Random rand, MutableBoundingBox sbb ) {
		if( function.startsWith( "chest" ) ) {
			TileEntity tileEntity = worldIn.getTileEntity( pos.down() );

			// Just another check to make sure everything is going well before we try to set the chest.
			if( tileEntity instanceof ChestTileEntity ) {
				// tileentity.rotate( this.rotation );
				// ((ChestTileEntity) tileentity).setLootTable(<resource_location_to_loottable>, rand.nextLong());
			}
		} else if( function.startsWith( "spawner" ) ) {
			TileEntity tileEntity = worldIn.getTileEntity( pos.down() );

			// Just another check to make sure everything is going well before we try to set the chest.
			if( tileEntity instanceof MobSpawnerTileEntity ) {
				EntityType< ? > entityType = function.equals( "spawner_keeper" ) ? SkyKeeperEntity.type : EntityType.PHANTOM;

				( ( MobSpawnerTileEntity )tileEntity ).getSpawnerBaseLogic().setEntityType( entityType );
				// ( ( MobSpawnerTileEntity )tileEntity ).getSpawnerBaseLogic().getCachedEntity()


				// ((ChestTileEntity) tileentity).setLootTable(<resource_location_to_loottable>, rand.nextLong());
			}
		}
	}

	/*
	 * Begins assembling your structure and where the pieces needs to go.
	 */
	public static void start( TemplateManager templateManager, BlockPos pos, Rotation rotation, List< StructurePiece > pieceList, Random random ) {
		int x = pos.getX();
		int z = pos.getZ();

		// This is how we factor in rotation for multi-piece structures.
		//
		// I would recommend using the OFFSET map above to have each piece at correct height relative of each other
		// and keep the X and Z equal to 0. And then in rotations, have the centermost piece have a rotation
		// of 0, 0, 0 and then have all other pieces' rotation be based off of the bottommost left corner of
		// that piece (the corner that is smallest in X and Z).
		//
		// Lots of trial and error may be needed to get this right for your structure.
		BlockPos rotationOffSet = new BlockPos( 0, 0, 0 ).rotate( rotation );
		BlockPos blockpos = rotationOffSet.add( x, pos.getY(), z );
		pieceList.add( new FlyingPhantomPiece( templateManager, blockpos, rotation ) );
	}

	private void setupPiece( TemplateManager templateManager ) {
		Template template = templateManager.getTemplateDefaulted( resourceLocation );
		PlacementSettings placementsettings = ( new PlacementSettings() ).setRotation( this.rotation )
			.setMirror( Mirror.NONE );
		this.setup( template, this.templatePosition, placementsettings );
	}
}