package com.majruszs_difficulty.features.undead_army;

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
	public static final double MAXIMUM_DISTANCE_TO_ARMY = 12000.0;
	private final List< UndeadArmy > undeadArmies = new ArrayList<>();
	private final List< UndeadArmyToBeSpawned > undeadArmiesToBeSpawned = new ArrayList<>();
	private ServerWorld world;
	private long ticksActive = 0L;

	public UndeadArmyManager( ServerWorld world ) {
		super( DATA_NAME );

		this.world = world;
	}

	/** Reads information about stored in memory Undead Armies. */
	@Override
	public void read( CompoundNBT nbt ) {
		this.ticksActive = nbt.getLong( UndeadArmyKeys.TICKS_ACTIVE );

		ListNBT listNBT = nbt.getList( UndeadArmyKeys.ARMIES, 10 );
		for( int i = 0; i < listNBT.size(); i++ )
			this.undeadArmies.add( new UndeadArmy( this.world, listNBT.getCompound( i ) ) );
	}

	/** Writes information about all current Undead Armies to memory. */
	@Override
	public CompoundNBT write( CompoundNBT compoundNBT ) {
		compoundNBT.putLong( UndeadArmyKeys.TICKS_ACTIVE, this.ticksActive );

		ListNBT listNBT = new ListNBT();
		for( UndeadArmy undeadArmy : this.undeadArmies ) {
			CompoundNBT nbt = new CompoundNBT();
			undeadArmy.write( nbt );
			listNBT.add( nbt );
		}
		compoundNBT.put( UndeadArmyKeys.ARMIES, listNBT );

		return compoundNBT;
	}

	@SubscribeEvent
	public static void onUpdate( TickEvent.ServerTickEvent event ) {
		if( event.side.isClient() || event.phase == TickEvent.Phase.END )
			return;

		RegistryHandler.UNDEAD_ARMY_MANAGER.tick();
	}

	/* ?????????????? */
	public void updateWorld( ServerWorld world ) {
		this.world = world;

		for( UndeadArmy undeadArmy : this.undeadArmies )
			undeadArmy.updateWorld( world );
	}

	/**
	 Spawns the Undead Army at player's position if possible.

	 @return Returns whether the Undead Army had spawned.
	 */
	public boolean spawn( PlayerEntity player ) {
		BlockPos attackPosition = getAttackPosition( player );
		UndeadArmyConfig config = Instances.UNDEAD_ARMY_CONFIG;

		if( findNearestUndeadArmy( attackPosition ) != null || isArmySpawningHere(
			attackPosition ) || config.isUndeadArmyDisabled() || !WorldHelper.isEntityIn( player, World.OVERWORLD ) )
			return false;

		this.undeadArmiesToBeSpawned.add( new UndeadArmyToBeSpawned( TimeConverter.secondsToTicks( 6.5 ), attackPosition, Direction.getRandom() ) );
		this.world.playSound( null, attackPosition, Instances.Sounds.UNDEAD_ARMY_APPROACHING, SoundCategory.AMBIENT, 0.25f, 1.0f );

		return true;
	}

	/** Updates information about armies that should be spawned and currently spawned. */
	public void tick() {
		this.ticksActive++;

		tickArmiesToBeSpawned();
		tickArmies();

		if( this.ticksActive % 200L == 0L )
			this.markDirty();
	}

	/**
	 Returns nearest Undead Army to given position.

	 @return May return null if there is none Undead Army or if one is very far away.
	 */
	@Nullable
	public UndeadArmy findNearestUndeadArmy( BlockPos position ) {
		UndeadArmy nearestArmy = null;
		double minimumDistance = MAXIMUM_DISTANCE_TO_ARMY;

		for( UndeadArmy undeadArmy : this.undeadArmies ) {
			double distanceToUndeadArmy = undeadArmy.getAttackPosition()
				.distanceSq( position );

			if( undeadArmy.isActive() && distanceToUndeadArmy < minimumDistance ) {
				nearestArmy = undeadArmy;
				minimumDistance = distanceToUndeadArmy;
			}
		}

		return nearestArmy;
	}

	/** Returns whether any Undead Army is spawning at given position. (when the sound is hearable) */
	public boolean isArmySpawningHere( BlockPos position ) {
		for( UndeadArmyToBeSpawned undeadArmyToBeSpawned : this.undeadArmiesToBeSpawned )
			if( undeadArmyToBeSpawned.position.distanceSq( position ) < MAXIMUM_DISTANCE_TO_ARMY )
				return true;

		return false;
	}

	/** Updates AI goals of Undead Army after reloading the game. */
	public void updateUndeadAIGoals() {
		for( UndeadArmy undeadArmy : this.undeadArmies )
			undeadArmy.updateNearbyUndeadAIGoals();
	}

	/** Updates all Undead Armies that will be spawned every tick. */
	private void tickArmiesToBeSpawned() {
		for( UndeadArmyToBeSpawned undeadArmyToBeSpawned : this.undeadArmiesToBeSpawned ) {
			undeadArmyToBeSpawned.ticksToSpawn--;

			if( undeadArmyToBeSpawned.ticksToSpawn == 0 )
				this.undeadArmies.add( new UndeadArmy( this.world, undeadArmyToBeSpawned.position, undeadArmyToBeSpawned.direction ) );
		}

		this.undeadArmiesToBeSpawned.removeIf( undeadArmyToBeSpawned->undeadArmyToBeSpawned.ticksToSpawn == 0 );
	}

	/** Updates all Undead Armies every tick. */
	private void tickArmies() {
		for( UndeadArmy undeadArmy : this.undeadArmies )
			undeadArmy.tick();

		if( this.ticksActive % 20L == 0L )
			this.undeadArmies.removeIf( undeadArmy->!undeadArmy.isActive() );
	}

	/** Returns position to attack depending on player's position. */
	private BlockPos getAttackPosition( PlayerEntity player ) {
		Optional< BlockPos > bedPosition = player.getBedPosition();
		BlockPos playerPosition = new BlockPos( player.getPositionVec() );
		BlockPos attackPosition = !bedPosition.isPresent() || playerPosition.distanceSq(
			bedPosition.get() ) >= MAXIMUM_DISTANCE_TO_ARMY ? playerPosition : bedPosition.get();

		int x = attackPosition.getX(), z = attackPosition.getZ();
		return new BlockPos( x, this.world.getHeight( Heightmap.Type.WORLD_SURFACE, x, z ), z );
	}

	/** Checks whether entity was spawned on Undead Army. */
	public boolean doesEntityBelongToUndeadArmy( LivingEntity entity ) {
		return UndeadArmy.doesEntityBelongToUndeadArmy( entity );
	}

	/** Struct that holds information where should Undead Army be spawned. (because firstly plays a sound and then after few seconds Undead Army will truly spawn) */
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