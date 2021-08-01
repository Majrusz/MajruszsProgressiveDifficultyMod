package com.majruszs_difficulty.generation.structure_pieces;

/** All possible Flying End Island pieces. */
public class FlyingEndShipPiece {

}
/*extends TemplateStructurePiece {
	public static final List< ResourceLocation > SHIP_RESOURCE_LOCATIONS = new ArrayList<>();
	public static final List< Block > RANDOM_BLOCKS = new ArrayList<>();
	public static final ResourceLocation SHIP_CHEST_RESOURCE_LOCATION = MajruszsDifficulty.getLocation( "chests/end_ship" );
	public static final ResourceLocation SHIP_EXTRA_CHEST_RESOURCE_LOCATION = MajruszsDifficulty.getLocation( "chests/end_ship_extra" );
	public static final ResourceLocation SHIP_MILITARY_RESOURCE_LOCATION = MajruszsDifficulty.getLocation( "chests/end_ship_military" );
	public static final ResourceLocation SHIP_MILITARY_SMALL_RESOURCE_LOCATION = MajruszsDifficulty.getLocation( "chests/end_ship_military_small" );
	public static final ResourceLocation SHIP_TRADER_RESOURCE_LOCATION = MajruszsDifficulty.getLocation( "chests/end_ship_trader" );

	static {
		SHIP_RESOURCE_LOCATIONS.add( MajruszsDifficulty.getLocation( "end_island_ship_military" ) );
		SHIP_RESOURCE_LOCATIONS.add( MajruszsDifficulty.getLocation( "end_island_ship_trader" ) );

		for( int i = 0; i < 40; i++ )
			RANDOM_BLOCKS.add( Blocks.IRON_BLOCK );
		for( int i = 0; i < 40; i++ )
			RANDOM_BLOCKS.add( Blocks.GOLD_BLOCK );
		for( int i = 0; i < 10; i++ )
			RANDOM_BLOCKS.add( Blocks.EMERALD_BLOCK );
		for( int i = 0; i < 5; i++ )
			RANDOM_BLOCKS.add( Blocks.DIAMOND_BLOCK );
		for( int i = 0; i < 5; i++ )
			RANDOM_BLOCKS.add( Blocks.ANCIENT_DEBRIS );
	}

	private final Rotation rotation;

	public FlyingEndShipPiece( TemplateManager templateManager, BlockPos position, Rotation rotation ) {
		super( Instances.FLYING_END_SHIP_PIECE, 0 );
		this.templatePosition = position;
		this.rotation = rotation;
		this.setupPiece( templateManager );
	}

	public FlyingEndShipPiece( TemplateManager templateManager, CompoundTag compoundNBT ) {
		super( Instances.FLYING_END_SHIP_PIECE, compoundNBT );
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
				chest.setLootTable( getLootTable( function ), random.nextLong() );
				if( function.startsWith( "chest_extra" ) && com.mlib.Random.tryChance( 0.75 ) )
					world.setBlockState( position.down(), Blocks.AIR.defaultBlockState(), 2 );
			}

			world.setBlockState( position, Blocks.AIR.defaultBlockState(), 2 );
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
		} else if( function.startsWith( "random_block" ) ) {
			Block randomBlock = RANDOM_BLOCKS.get( MajruszLibrary.RANDOM.nextInt( RANDOM_BLOCKS.size() ) );
			world.setBlockState( position, randomBlock.defaultBlockState(), 2 );
		}
	}

	// /** Begins assembling your structure and where the pieces needs to go. /
	public static void start( TemplateManager templateManager, BlockPos position, Rotation rotation, List< StructurePiece > pieces, Random random ) {
		BlockPos rotationOffSet = new BlockPos( 0, 0, 0 ).rotate( rotation );
		BlockPos blockpos = rotationOffSet.add( position.getX(), position.getY(), position.getZ() );

		pieces.add( new FlyingEndShipPiece( templateManager, blockpos, rotation ) );
	}

	// /** Returns loot table resource location depending on given id. /
	private ResourceLocation getLootTable( String id ) {
		switch( id ) {
			default:
			case "chest_ship":
				return SHIP_CHEST_RESOURCE_LOCATION;
			case "chest_extra":
				return SHIP_EXTRA_CHEST_RESOURCE_LOCATION;
			case "chest_military":
				return SHIP_MILITARY_RESOURCE_LOCATION;
			case "chest_military_small":
				return SHIP_MILITARY_SMALL_RESOURCE_LOCATION;
			case "chest_trader":
				return SHIP_TRADER_RESOURCE_LOCATION;
		}
	}

	private void setupPiece( TemplateManager templateManager ) {
		Template template = templateManager.getTemplateDefaulted( getRandomResourceLocation() );
		PlacementSettings placementsettings = ( new PlacementSettings() ).setRotation( this.rotation )
			.setMirror( Mirror.NONE );

		this.setup( template, this.templatePosition, placementsettings );
	}

	// /** Returns random resource location to building. /
	private ResourceLocation getRandomResourceLocation() {
		return SHIP_RESOURCE_LOCATIONS.get( MajruszLibrary.RANDOM.nextInt( SHIP_RESOURCE_LOCATIONS.size() ) );
	}
}*/