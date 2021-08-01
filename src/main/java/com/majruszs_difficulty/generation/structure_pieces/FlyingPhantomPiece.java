package com.majruszs_difficulty.generation.structure_pieces;

/** Flying phantom piece. */
public class FlyingPhantomPiece {}

/*extends TemplateStructurePiece {
	public static final ResourceLocation RESOURCE_LOCATION = MajruszsDifficulty.getLocation( "flying_phantom" );
	public static final ResourceLocation CHEST_RESOURCE_LOCATION = MajruszsDifficulty.getLocation( "chests/flying_phantom" );
	private final Rotation rotation;

	public FlyingPhantomPiece( TemplateManager templateManager, BlockPos position, Rotation rotation ) {
		super( Instances.FLYING_PHANTOM_PIECE, 0 );
		this.templatePosition = position;
		this.rotation = rotation;
		this.setupPiece( templateManager );
	}

	public FlyingPhantomPiece( TemplateManager templateManager, CompoundTag compoundNBT ) {
		super( Instances.FLYING_PHANTOM_PIECE, compoundNBT );
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

			if( tileEntity instanceof ChestTileEntity )
				( ( ChestTileEntity )tileEntity ).setLootTable( CHEST_RESOURCE_LOCATION, random.nextLong() );
		} else if( function.startsWith( "sky_keeper" ) ) {
			SkyKeeperEntity monster = SkyKeeperEntity.type.create( world.getLevel() );
			if( monster != null ) {
				monster.setPersistenceRequired();
				monster.setPosition( position.getX(), position.getY() + 3, position.getZ() );
				world.addFreshEntity( monster );
			}
		} else if( function.startsWith( "phantom" ) ) {
			Phantom monster = EntityType.PHANTOM.create( world.getLevel() );
			if( monster != null ) {
				monster.setPersistenceRequired();
				monster.setPosition( position.getX(), position.getY() + 3, position.getZ() );
				world.addFreshEntity( monster );
			}
		} else
			return;

		world.setBlockState( position, Blocks.AIR.defaultBlockState(), 2 );
	}

	// /** Begins assembling your structure and where the pieces needs to go. /
	public static void start( TemplateManager templateManager, BlockPos position, Rotation rotation, List< StructurePiece > pieces, Random random ) {
		BlockPos rotationOffSet = new BlockPos( 0, 0, 0 ).rotate( rotation );
		BlockPos blockpos = rotationOffSet.add( position.getX(), position.getY(), position.getZ() );

		pieces.add( new FlyingPhantomPiece( templateManager, blockpos, rotation ) );
	}

	private void setupPiece( TemplateManager templateManager ) {
		Template template = templateManager.getTemplateDefaulted( RESOURCE_LOCATION );
		PlacementSettings placementsettings = ( new PlacementSettings() ).setRotation( this.rotation )
			.setMirror( Mirror.NONE );

		this.setup( template, this.templatePosition, placementsettings );
	}
}*/