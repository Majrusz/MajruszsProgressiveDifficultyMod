package com.majruszs_difficulty.structure_pieces;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.entities.SkyKeeperEntity;
import com.mlib.MajruszLibrary;
import com.mlib.config.DoubleConfig;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.entity.monster.ShulkerEntity;
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
import net.minecraft.world.spawner.AbstractSpawner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** All possible Flying End Island pieces. */
public class FlyingEndIslandPiece extends TemplateStructurePiece {
	public static final List< ResourceLocation > BUILDING_ISLAND_RESOURCE_LOCATIONS = new ArrayList<>();
	public static final List< ResourceLocation > EMPTY_ISLAND_RESOURCE_LOCATIONS = new ArrayList<>();
	public static final ResourceLocation DUNGEON_CHEST_RESOURCE_LOCATION = MajruszsDifficulty.getLocation( "chests/end_island_dungeon" );
	public static final ResourceLocation TOWER_CHEST_RESOURCE_LOCATION = MajruszsDifficulty.getLocation( "chests/end_island_tower" );
	public static final ResourceLocation ARCHER_CHEST_RESOURCE_LOCATION = MajruszsDifficulty.getLocation( "chests/end_island_archer_tower" );
	public static final ResourceLocation POTION_CHEST_RESOURCE_LOCATION = MajruszsDifficulty.getLocation( "chests/end_island_potion" );
	private final Rotation rotation;

	static {
		BUILDING_ISLAND_RESOURCE_LOCATIONS.add( MajruszsDifficulty.getLocation( "end_island_tower_1" ) );
		BUILDING_ISLAND_RESOURCE_LOCATIONS.add( MajruszsDifficulty.getLocation( "end_island_tower_2" ) );
		BUILDING_ISLAND_RESOURCE_LOCATIONS.add( MajruszsDifficulty.getLocation( "end_island_dungeon" ) );
		BUILDING_ISLAND_RESOURCE_LOCATIONS.add( MajruszsDifficulty.getLocation( "end_island_alchemist" ) );
		BUILDING_ISLAND_RESOURCE_LOCATIONS.add( MajruszsDifficulty.getLocation( "end_island_archer_tower" ) );
		BUILDING_ISLAND_RESOURCE_LOCATIONS.add( MajruszsDifficulty.getLocation( "end_island_forge" ) );

		EMPTY_ISLAND_RESOURCE_LOCATIONS.add( MajruszsDifficulty.getLocation( "end_island_empty_1" ) );
		EMPTY_ISLAND_RESOURCE_LOCATIONS.add( MajruszsDifficulty.getLocation( "end_island_empty_2" ) );
		EMPTY_ISLAND_RESOURCE_LOCATIONS.add( MajruszsDifficulty.getLocation( "end_island_empty_3" ) );
		EMPTY_ISLAND_RESOURCE_LOCATIONS.add( MajruszsDifficulty.getLocation( "end_island_empty_4" ) );
	}

	public FlyingEndIslandPiece( TemplateManager templateManager, BlockPos position, Rotation rotation ) {
		super( Instances.FLYING_END_ISLAND_PIECE, 0 );
		this.templatePosition = position;
		this.rotation = rotation;
		this.setupPiece( templateManager );
	}

	public FlyingEndIslandPiece( TemplateManager templateManager, CompoundNBT compoundNBT ) {
		super( Instances.FLYING_END_ISLAND_PIECE, compoundNBT );
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
			TileEntity tileEntity = world.getTileEntity( position.down() );

			if( tileEntity instanceof ChestTileEntity ) {
				ChestTileEntity chest = ( ChestTileEntity )tileEntity;
				if( function.startsWith( "chest_dung" ) ) {
					if( com.mlib.Random.tryChance( 0.3 ) )
						chest.setLootTable( DUNGEON_CHEST_RESOURCE_LOCATION, random.nextLong() );
					else
						world.setBlockState( position.down(), Blocks.AIR.getDefaultState(), 2 );

				} else if( function.startsWith( "chest_tower" ) ) {
					chest.setLootTable( TOWER_CHEST_RESOURCE_LOCATION, random.nextLong() );
				} else if( function.startsWith( "chest_archer" ) ) {
					chest.setLootTable( ARCHER_CHEST_RESOURCE_LOCATION, random.nextLong() );
				} else if( function.startsWith( "chest_potion" ) ) {
					chest.setLootTable( POTION_CHEST_RESOURCE_LOCATION, random.nextLong() );
				}
			}
			world.setBlockState( position, Blocks.AIR.getDefaultState(), 2 );

		} else if( function.startsWith( "shulker" ) ) {
			world.setBlockState( position, Blocks.AIR.getDefaultState(), 2 );
			ShulkerEntity monster = EntityType.SHULKER.create( world.getWorld() );
			if( monster != null ) {
				monster.enablePersistence();
				monster.setPosition( position.getX(), position.getY(), position.getZ() );
				world.addEntity( monster );
			}
		} else if( function.startsWith( "spawner" ) ) {
			world.setBlockState( position, Blocks.AIR.getDefaultState(), 2 );
			world.setBlockState( position.down(), Blocks.SPAWNER.getDefaultState(), 2 );
			TileEntity tileEntity = world.getTileEntity( position.down() );

			if( tileEntity instanceof MobSpawnerTileEntity ) {
				MobSpawnerTileEntity mobSpawnerTileEntity = ( MobSpawnerTileEntity )tileEntity;
				AbstractSpawner abstractSpawner = mobSpawnerTileEntity.getSpawnerBaseLogic();
				abstractSpawner.setEntityType( EntityType.ENDERMITE );
			}
		}
	}

	/** Begins assembling your structure and where the pieces needs to go. */
	public static void start( TemplateManager templateManager, BlockPos position, Rotation rotation, List< StructurePiece > pieces, Random random ) {
		BlockPos rotationOffSet = new BlockPos( 0, 0, 0 ).rotate( rotation );
		BlockPos blockpos = rotationOffSet.add( position.getX(), position.getY(), position.getZ() );

		pieces.add( new FlyingEndIslandPiece( templateManager, blockpos, rotation ) );
	}

	private void setupPiece( TemplateManager templateManager ) {
		Template template = templateManager.getTemplateDefaulted( getRandomResourceLocation() );
		PlacementSettings placementsettings = ( new PlacementSettings() ).setRotation( this.rotation )
			.setMirror( Mirror.NONE );

		this.setup( template, this.templatePosition, placementsettings );
	}

	private ResourceLocation getRandomResourceLocation() {
		if( com.mlib.Random.tryChance( Instances.FLYING_END_ISLAND.buildingIslandChance.get() ) )
			return BUILDING_ISLAND_RESOURCE_LOCATIONS.get( MajruszLibrary.RANDOM.nextInt( BUILDING_ISLAND_RESOURCE_LOCATIONS.size() ) );
		else
			return EMPTY_ISLAND_RESOURCE_LOCATIONS.get( MajruszLibrary.RANDOM.nextInt( EMPTY_ISLAND_RESOURCE_LOCATIONS.size() ) );
	}
}