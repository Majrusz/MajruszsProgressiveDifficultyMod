package com.majruszs_difficulty.events.undead_army;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.RegistryHandler;
import com.mlib.TimeConverter;
import com.mlib.WorldHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

/** Handling all Undead Armies in the world. */
@Mod.EventBusSubscriber
public class UndeadArmyManager extends WorldSavedData {
	public static final String DATA_NAME = "undead_army";
	public static final double maximumDistanceToArmy = 12000.0;
	private ServerWorld world;
	private final List< UndeadArmy > undeadArmies = new ArrayList<>();
	private final List< UndeadArmyToBeSpawned > undeadArmiesToBeSpawned = new ArrayList<>();
	private long ticksActive = 0L;

	public UndeadArmyManager( ServerWorld world ) {
		super( DATA_NAME );

		this.world = world;
	}

	@Override
	public void load( CompoundNBT nbt ) {
		this.ticksActive = nbt.getLong( "TicksActive" );
		ListNBT listNBT = nbt.getList( "Armies", 10 );

		for( int i = 0; i < listNBT.size(); i++ )
			this.undeadArmies.add( new UndeadArmy( this.world, listNBT.getCompound( i ) ) );
	}

	@Override
	public CompoundNBT save( CompoundNBT compoundNBT ) {
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

	public boolean spawn( PlayerEntity player ) {
		BlockPos attackPosition = getAttackPosition( player );

		if( findUndeadArmy( attackPosition ) != null || isArmySpawningHere(
			attackPosition ) || Instances.UNDEAD_ARMY_CONFIG.availability.isDisabled() )
			return false;

		if( !WorldHelper.isEntityIn( player, World.OVERWORLD ) )
			return false;

		this.undeadArmiesToBeSpawned.add( new UndeadArmyToBeSpawned( TimeConverter.secondsToTicks( 6.5 ), attackPosition, Direction.getRandom() ) );
		this.world.playSound( null, attackPosition, Instances.Sounds.UNDEAD_ARMY_APPROACHING, SoundCategory.AMBIENT, 0.25f, 1.0f );

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

		RegistryHandler.UNDEAD_ARMY_MANAGER.tick();
	}

	public void updateUndeadGoals() {
		for( UndeadArmy undeadArmy : this.undeadArmies )
			undeadArmy.updateNearbyUndeadGoals();
	}

	private void tickArmiesToBeSpawned() {
		for( UndeadArmyToBeSpawned undeadArmyToBeSpawned : this.undeadArmiesToBeSpawned ) {
			undeadArmyToBeSpawned.ticksToSpawn--;

			if( undeadArmyToBeSpawned.ticksToSpawn == 0 )
				this.undeadArmies.add( new UndeadArmy( this.world, undeadArmyToBeSpawned.position, undeadArmyToBeSpawned.direction ) );
		}

		this.undeadArmiesToBeSpawned.removeIf( undeadArmyToBeSpawned->undeadArmyToBeSpawned.ticksToSpawn == 0 );
	}

	private void tickArmies() {
		for( UndeadArmy undeadArmy : this.undeadArmies )
			undeadArmy.tick();

		if( this.ticksActive % 20L == 0L )
			this.undeadArmies.removeIf( undeadArmy->!undeadArmy.isActive() );
	}

	private BlockPos getAttackPosition( PlayerEntity player ) {
		Optional< BlockPos > bedPosition = player.getBedPosition();
		BlockPos playerPosition = new BlockPos( player.getPositionVec() );

		BlockPos attackPosition;
		if( !bedPosition.isPresent() || playerPosition.distanceSq( bedPosition.get() ) >= maximumDistanceToArmy )
			attackPosition = playerPosition;
		else
			attackPosition = bedPosition.get();

		int x = attackPosition.getX();
		int z = attackPosition.getZ();
		int y = this.world.getHeight( Heightmap.Type.WORLD_SURFACE, x, z );

		return new BlockPos( x, y, z );
	}

	/** Checks whether entity was spawned on Undead Army. */
	public boolean doesEntityBelongToUndeadArmy( LivingEntity entity ) {
		return UndeadArmy.doesEntityBelongToUndeadArmy( entity );
	}

	public static class UndeadArmyToBeSpawned {
		public int ticksToSpawn;
		public BlockPos position;
		public Direction direction;

		public UndeadArmyToBeSpawned( int ticksToSpawn, BlockPos position, Direction direction ) {
			this.ticksToSpawn = ticksToSpawn;
			this.position = position;
			this.direction = direction;
		}
	}
}