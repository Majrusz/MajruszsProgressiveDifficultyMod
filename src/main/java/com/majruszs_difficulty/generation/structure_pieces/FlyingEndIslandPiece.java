package com.majruszs_difficulty.generation.structure_pieces;

/** All possible Flying End Island pieces. */
public class FlyingEndIslandPiece {

}
/*
public class FlyingEndIslandPiece extends TemplateStructurePiece {
	public static final List< ResourceLocation > BUILDING_ISLAND_RESOURCE_LOCATIONS = new ArrayList<>();
	public static final List< ResourceLocation > EMPTY_ISLAND_RESOURCE_LOCATIONS = new ArrayList<>();
	public static final ResourceLocation DUNGEON_CHEST_RESOURCE_LOCATION = MajruszsDifficulty.getLocation( "chests/end_island_dungeon" );
	public static final ResourceLocation TOWER_CHEST_RESOURCE_LOCATION = MajruszsDifficulty.getLocation( "chests/end_island_tower" );
	public static final ResourceLocation ARCHER_CHEST_RESOURCE_LOCATION = MajruszsDifficulty.getLocation( "chests/end_island_archer_tower" );
	public static final ResourceLocation POTION_CHEST_RESOURCE_LOCATION = MajruszsDifficulty.getLocation( "chests/end_island_potion" );
	public static final ResourceLocation FORGE_CHEST_RESOURCE_LOCATION = MajruszsDifficulty.getLocation( "chests/end_island_forge" );
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

	public FlyingEndIslandPiece( StructureManager templateManager, BlockPos position, Rotation rotation ) {
		super( Instances.FLYING_END_ISLAND_PIECE, 0 );
		this.templatePosition = position;
		this.rotation = rotation;
		this.setupPiece( templateManager );
	}

	public FlyingEndIslandPiece( StructureManager templateManager, CompoundTag compoundNBT ) {
		super( Instances.FLYING_END_ISLAND_PIECE, compoundNBT );
		this.rotation = Rotation.valueOf( compoundNBT.getString( "Rot" ) );
		this.setupPiece( templateManager );
	}

	@Override
	protected void readAdditional( CompoundTag compoundNBT ) {
		super.readAdditional( compoundNBT );

		compoundNBT.putString( "Rot", this.rotation.name() );
	}

	@Override
	protected void handleDataMarker( String function, BlockPos position, IServerLevel world, Random random, MutableBoundingBox boundingBox ) {
		if( function.startsWith( "chest" ) ) {
			TileEntity tileEntity = world.getTileEntity( position.down() );

			if( tileEntity instanceof ChestTileEntity ) {
				ChestTileEntity chest = ( ChestTileEntity )tileEntity;
				if( function.startsWith( "chest_dung" ) ) {
					if( com.mlib.Random.tryChance( 0.3 ) )
						chest.setLootTable( DUNGEON_CHEST_RESOURCE_LOCATION, random.nextLong() );
					else
						world.setBlockState( position.down(), Blocks.AIR.defaultBlockState(), 2 );

				} else if( function.startsWith( "chest_tower" ) ) {
					chest.setLootTable( TOWER_CHEST_RESOURCE_LOCATION, random.nextLong() );
				} else if( function.startsWith( "chest_archer" ) ) {
					chest.setLootTable( ARCHER_CHEST_RESOURCE_LOCATION, random.nextLong() );
				} else if( function.startsWith( "chest_potion" ) ) {
					chest.setLootTable( POTION_CHEST_RESOURCE_LOCATION, random.nextLong() );
				} else if( function.startsWith( "chest_forge" ) ) {
					chest.setLootTable( FORGE_CHEST_RESOURCE_LOCATION, random.nextLong() );
				}
			}
			world.setBlockState( position, Blocks.AIR.defaultBlockState(), 2 );

		} else if( function.startsWith( "shulker" ) ) {
			world.setBlockState( position, Blocks.AIR.defaultBlockState(), 2 );
			Shulker monster = EntityType.SHULKER.create( world.getLevel() );
			if( monster != null ) {
				monster.setPersistenceRequired();
				monster.setPosition( position.getX(), position.getY(), position.getZ() );
				world.addFreshEntity( monster );
			}
		} else if( function.startsWith( "end_keeper" ) ) {
			world.setBlockState( position, Blocks.AIR.defaultBlockState(), 2 );
			SkyKeeperEntity monster = SkyKeeperEntity.type.create( world.getLevel() );
			if( monster != null ) {
				monster.setPersistenceRequired();
				monster.setPosition( position.getX(), position.getY(), position.getZ() );
				world.addFreshEntity( monster );
			}
		} else if( function.startsWith( "spawner" ) ) {
			world.setBlockState( position, Blocks.AIR.defaultBlockState(), 2 );
			world.setBlockState( position.down(), Blocks.SPAWNER.defaultBlockState(), 2 );
			TileEntity tileEntity = world.getTileEntity( position.down() );

			if( tileEntity instanceof SpawnerBlockEntity ) {
				SpawnerBlockEntity mobSpawnerTileEntity = ( SpawnerBlockEntity )tileEntity;
				AbstractSpawner abstractSpawner = mobSpawnerTileEntity.getSpawnerBaseLogic();
				abstractSpawner.setEntityType( EntityType.ENDERMITE );
			}
		}
	}

	// /** Begins assembling your structure and where the pieces needs to go. /
	public static void start( StructureManager templateManager, BlockPos position, Rotation rotation, List< StructurePiece > pieces, Random random ) {
		BlockPos rotationOffSet = new BlockPos( 0, 0, 0 ).rotate( rotation );
		BlockPos blockpos = rotationOffSet.add( position.getX(), position.getY(), position.getZ() );

		pieces.add( new FlyingEndIslandPiece( templateManager, blockpos, rotation ) );
	}

	private void setupPiece( StructureManager templateManager ) {
		StructureTemplate template = templateManager.getOrCreate( getRandomResourceLocation() );
		StructurePlaceSettings placementsettings = ( new StructurePlaceSettings() ).setRotation( this.rotation )
			.setMirror( Mirror.NONE );

		this.setup( template, this.templatePosition, placementsettings );
	}

	// /** Returns random resource location to building. /
	private ResourceLocation getRandomResourceLocation() {
		if( com.mlib.Random.tryChance( Instances.FLYING_END_ISLAND.buildingIslandChance.get() ) ) {
			return BUILDING_ISLAND_RESOURCE_LOCATIONS.get( MajruszLibrary.RANDOM.nextInt( BUILDING_ISLAND_RESOURCE_LOCATIONS.size() ) );
		} else
			return EMPTY_ISLAND_RESOURCE_LOCATIONS.get( MajruszLibrary.RANDOM.nextInt( EMPTY_ISLAND_RESOURCE_LOCATIONS.size() ) );
	}
}*/