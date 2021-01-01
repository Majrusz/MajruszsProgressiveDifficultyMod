package com.majruszs_difficulty.events.undead_army;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.RegistryHandler;
import com.majruszs_difficulty.events.UndeadArmy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber
public class UndeadArmyManager extends WorldSavedData {
	public static final String DATA_NAME = "undead_army";
	public static final double maximumDistanceToArmy = 9001.0;
	private ServerWorld world;
	private final List< UndeadArmy > undeadArmies = new ArrayList<>();
	private final List< UndeadArmyToBeSpawned > undeadArmiesToBeSpawned = new ArrayList<>();
	private long ticksActive = 0L;

	public UndeadArmyManager( ServerWorld world ) {
		super( DATA_NAME );

		this.world = world;
	}

	@Override
	public void read( CompoundNBT nbt ) {
		this.ticksActive = nbt.getLong( "TicksActive" );
		ListNBT listNBT = nbt.getList( "Armies", 10 );

		for( int i = 0; i < listNBT.size(); i++ )
			this.undeadArmies.add( new UndeadArmy( this.world, listNBT.getCompound( i ) ) );
	}

	@Override
	public CompoundNBT write( CompoundNBT compoundNBT ) {
		compoundNBT.putLong( "Tick", this.ticksActive );
		ListNBT listNBT = new ListNBT();

		for( UndeadArmy undeadArmy : this.undeadArmies ) {
			CompoundNBT nbt = new CompoundNBT();
			undeadArmy.write( nbt );
			listNBT.add( nbt );
		}

		compoundNBT.put( "Armies", listNBT );
		return compoundNBT;
	}

	public void updateWorld( ServerWorld world ) {
		this.world = world;

		for( UndeadArmy undeadArmy : this.undeadArmies )
			undeadArmy.updateWorld( world );
	}

	public boolean spawn( PlayerEntity player, ServerWorld world ) {
		BlockPos attackPosition = getAttackPosition( player );

		if( findUndeadArmy( attackPosition ) != null || isArmySpawningHere( attackPosition ) )
			return false;

		if( this.world.isDaytime() || !MajruszsHelper.isPlayerIn( player, DimensionType.OVERWORLD ) )
			return false;

		this.undeadArmiesToBeSpawned.add(
			new UndeadArmyToBeSpawned( MajruszsHelper.secondsToTicks( 6.5 ), attackPosition, UndeadArmy.Direction.getRandom() ) );

		this.world.playSound( null, attackPosition, RegistryHandler.UNDEAD_ARMY_APPROACHING.get(), SoundCategory.AMBIENT, 0.25f, 1.0f );
		MajruszsDifficulty.LOGGER.info( "Spawned undead army! " + attackPosition );

		return true;
	}

	public void tick() {
		this.ticksActive++;

		tickArmiesToBeSpawned();
		tickArmies();

		if( this.ticksActive % 200L == 0L )
			this.markDirty();
	}

	@Nullable
	public UndeadArmy findUndeadArmy( BlockPos position ) {
		UndeadArmy nearestArmy = null;
		double maximumDistance = maximumDistanceToArmy;

		for( UndeadArmy undeadArmy : this.undeadArmies ) {
			double distance = undeadArmy.getPosition()
				.distanceSq( position );

			if( undeadArmy.isActive() && distance < maximumDistance ) {
				nearestArmy = undeadArmy;
				maximumDistance = distance;
			}
		}

		return nearestArmy;
	}

	public boolean isArmySpawningHere( BlockPos position ) {
		for( UndeadArmyToBeSpawned undeadArmyToBeSpawned : this.undeadArmiesToBeSpawned )
			if( undeadArmyToBeSpawned.position.distanceSq( position ) < maximumDistanceToArmy )
				return true;

		return false;
	}

	@SubscribeEvent
	public static void onUpdate( TickEvent.ServerTickEvent event ) {
		if( event.side.isClient() || event.phase == TickEvent.Phase.END )
			return;

		RegistryHandler.undeadArmyManager.tick();
	}

	public void updateUndeadGoals() {
		for( UndeadArmy undeadArmy : this.undeadArmies )
			undeadArmy.updateNearbyUndeadGoals();
	}

	private void tickArmiesToBeSpawned() {
		for( UndeadArmyToBeSpawned undeadArmyToBeSpawned : this.undeadArmiesToBeSpawned ) {
			undeadArmyToBeSpawned.ticksToSpawn--;

			if( undeadArmyToBeSpawned.ticksToSpawn == 0 ) {
				this.undeadArmies.add( new UndeadArmy( this.world, undeadArmyToBeSpawned.position, undeadArmyToBeSpawned.direction ) );
				notifyAllPlayers( undeadArmyToBeSpawned.direction, undeadArmyToBeSpawned.position );
			}
		}

		this.undeadArmiesToBeSpawned.removeIf( undeadArmyToBeSpawned->undeadArmyToBeSpawned.ticksToSpawn == 0 );
	}

	private void tickArmies() {
		for( UndeadArmy undeadArmy : this.undeadArmies )
			undeadArmy.tick();

		if( this.ticksActive % 20L == 0L )
			this.undeadArmies.removeIf( undeadArmy->!undeadArmy.isActive() );
	}

	/*public void updateArmyGoal() {
		MajruszsDifficulty.LOGGER.info( "Ready!" );

		int i = 0;
		for( Entity entity : this.world.getEntities( null, entity -> entity.getPersistentData().contains( "UndeadArmyFrostWalker" ) ) ) { //this.world.getEntities( null, entity->!entity.getPersistentData().contains( "UndeadArmyFrostWalker" ) ) ) {
			if( !( entity instanceof MonsterEntity ) )
				return;

			MonsterEntity monster = ( MonsterEntity )entity;
			CompoundNBT data = monster.getPersistentData();

			int x = data.getInt( "UndeadArmyPositionX" );
			int y = data.getInt( "UndeadArmyPositionY" );
			int z = data.getInt( "UndeadArmyPositionZ" );

			BlockPos positionToAttack = new BlockPos( x, y, z );
			monster.goalSelector.addGoal( 0, new UndeadAttackPositionGoal( monster, positionToAttack, 1.0f, 25.0f, 10.0f ) );
			i++;
		}

		MajruszsDifficulty.LOGGER.info( "Done! " + i + "/" + this.world.getEntities().count() );
		for( Entity entity : this.world.getEntities(). )
		MajruszsDifficulty.LOGGER.info( "Ready!" );
		double startX = this.positionToAttack.getX() - spawnRadius;
		double startY = this.positionToAttack.getY() - spawnRadius;
		double startZ = this.positionToAttack.getZ() - spawnRadius;
		double endX = this.positionToAttack.getX() + spawnRadius;
		double endY = this.positionToAttack.getY() + spawnRadius;
		double endZ = this.positionToAttack.getZ() + spawnRadius;
		AxisAlignedBB axis = new AxisAlignedBB( startX, startY, startZ, startX+1, startY+1, startZ+1 ).grow( spawnRadius * spawnRadius );

		int i = 0;
		for( Entity entity : this.world.getEntitiesWithinAABB( Entity.class, axis ) ) {
			i++;
			if( !( entity instanceof MonsterEntity ) )
				return;

			MonsterEntity monster = ( MonsterEntity )entity;
			monster.goalSelector.addGoal( 0, new UndeadAttackPositionGoal( monster, this.positionToAttack, 1.0f, 25.0f, 10.0f ) );

			CompoundNBT nbt = monster.getPersistentData();
			nbt.putBoolean( "UndeadArmyFrostWalker", true );
			nbt.putInt( "UndeadArmyPositionX", this.positionToAttack.getX() );
			nbt.putInt( "UndeadArmyPositionY", this.positionToAttack.getY() );
			nbt.putInt( "UndeadArmyPositionZ", this.positionToAttack.getZ() );
		}
		MajruszsDifficulty.LOGGER.info( "Done! " + i + "/" + this.world.getEntities().count() );
	}*/

	private BlockPos getAttackPosition( PlayerEntity player ) {
		Optional< BlockPos > bedPosition = player.getBedPosition();
		BlockPos playerPosition = new BlockPos( player.getPositionVec() );

		BlockPos attackPosition;

		if( !bedPosition.isPresent() )
			attackPosition = playerPosition;
		else if( playerPosition.distanceSq( bedPosition.get() ) >= maximumDistanceToArmy )
			attackPosition = playerPosition;
		else
			attackPosition = bedPosition.get();

		int x = attackPosition.getX();
		int z = attackPosition.getZ();
		int y = this.world.getHeight( Heightmap.Type.WORLD_SURFACE, x, z );

		return new BlockPos( x, y, z );
	}

	private void notifyAllPlayers( UndeadArmy.Direction direction, BlockPos position ) {
		IFormattableTextComponent message = getMessage( direction );
		for( PlayerEntity player : this.world.getPlayers(
			player->player.getDistanceSq( Vector3d.copyCentered( position ) ) < maximumDistanceToArmy ) )
			player.sendStatusMessage( message, false );
	}

	private static IFormattableTextComponent getMessage( UndeadArmy.Direction direction ) {
		IFormattableTextComponent message = new TranslationTextComponent( "majruszs_difficulty.undead_army.approaching" );
		message.appendString( " " );
		message.append( new TranslationTextComponent( "majruszs_difficulty.undead_army." + direction.toString()
			.toLowerCase() ) );
		message.appendString( "!" );
		message.mergeStyle( TextFormatting.BOLD );
		message.mergeStyle( TextFormatting.DARK_PURPLE );

		return message;
	}

	public static class UndeadArmyToBeSpawned {
		public int ticksToSpawn;
		public BlockPos position;
		public UndeadArmy.Direction direction;

		public UndeadArmyToBeSpawned( int ticksToSpawn, BlockPos position, UndeadArmy.Direction direction ) {
			this.ticksToSpawn = ticksToSpawn;
			this.position = position;
			this.direction = direction;
		}
	}
}