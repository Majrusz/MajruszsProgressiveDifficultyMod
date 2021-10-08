package com.majruszs_difficulty.features.undead_army;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.RegistryHandler;
import com.mlib.LevelHelper;
import com.mlib.MajruszLibrary;
import com.mlib.TimeConverter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Handling all Undead Armies in the world. */
@Mod.EventBusSubscriber
public class UndeadArmyManager extends SavedData {
	public static final String DATA_NAME = "undead_army";
	public static final double MAXIMUM_DISTANCE_TO_ARMY = 12000.0;
	private final List< UndeadArmy > undeadArmies = new ArrayList<>();
	private final List< UndeadArmyToBeSpawned > undeadArmiesToBeSpawned = new ArrayList<>();
	private ServerLevel level;
	private long ticksActive = 0L;

	public UndeadArmyManager( ServerLevel world ) {
		this.level = world;
	}

	/** Writes information about all current Undead Armies to memory. */
	@Override
	public CompoundTag save( CompoundTag compoundNBT ) {
		compoundNBT.putLong( UndeadArmyKeys.TICKS_ACTIVE, this.ticksActive );

		ListTag listNBT = new ListTag();
		for( UndeadArmy undeadArmy : this.undeadArmies ) {
			CompoundTag nbt = new CompoundTag();
			undeadArmy.write( nbt );
			listNBT.add( nbt );
		}
		compoundNBT.put( UndeadArmyKeys.ARMIES, listNBT );

		return compoundNBT;
	}

	/** Reads information about stored in memory Undead Armies. */
	public static UndeadArmyManager load( CompoundTag nbt, ServerLevel level ) {
		UndeadArmyManager armyManager = new UndeadArmyManager( level );
		armyManager.ticksActive = nbt.getLong( UndeadArmyKeys.TICKS_ACTIVE );

		ListTag listNBT = nbt.getList( UndeadArmyKeys.ARMIES, 10 );
		for( int i = 0; i < listNBT.size(); i++ )
			armyManager.undeadArmies.add( new UndeadArmy( armyManager.level, listNBT.getCompound( i ) ) );

		return armyManager;
	}

	@SubscribeEvent
	public static void onUpdate( TickEvent.ServerTickEvent event ) {
		if( event.side.isClient() || event.phase == TickEvent.Phase.END )
			return;

		if( RegistryHandler.UNDEAD_ARMY_MANAGER != null )
			RegistryHandler.UNDEAD_ARMY_MANAGER.tick();
	}

	/* ?????????????? */
	public void updateWorld( ServerLevel world ) {
		this.level = world;

		for( UndeadArmy undeadArmy : this.undeadArmies )
			undeadArmy.updateWorld( world );
	}

	/**
	 Spawns the Undead Army at player's position if possible.

	 @return Returns whether the Undead Army had spawned.
	 */
	public boolean tryToSpawn( Player player ) {
		return LevelHelper.isEntityIn( player, Level.OVERWORLD ) && tryToSpawn( getAttackPosition( player ) );
	}

	/**
	 Spawns the Undead Army at given position if possible.

	 @return Returns whether the Undead Army had spawned.
	 */
	public boolean tryToSpawn( BlockPos attackPosition ) {
		return tryToSpawn( attackPosition, Optional.empty() );
	}

	/**
	 Spawns the Undead Army at given position and from given direction if possible.

	 @return Returns whether the Undead Army had spawned.
	 */
	public boolean tryToSpawn( BlockPos attackPosition, Optional< Direction > optionalDirection ) {
		UndeadArmyConfig config = Instances.UNDEAD_ARMY_CONFIG;

		if( findNearestUndeadArmy( attackPosition ) != null || isArmySpawningHere( attackPosition ) || config.isUndeadArmyDisabled() )
			return false;

		Direction direction = optionalDirection.orElse( Direction.getRandom() );
		this.undeadArmiesToBeSpawned.add( new UndeadArmyToBeSpawned( TimeConverter.secondsToTicks( 6.5 ), attackPosition, direction ) );
		this.level.playSound( null, attackPosition, Instances.Sounds.UNDEAD_ARMY_APPROACHING, SoundSource.AMBIENT, 0.25f, 1.0f );
		MajruszLibrary.LOGGER.info( "Undead Army started at " + attackPosition + "!" );

		return true;
	}

	/** Updates information about armies that should be spawned and currently spawned. */
	public void tick() {
		this.ticksActive++;

		tickArmiesToBeSpawned();
		tickArmies();

		if( this.ticksActive % 200L == 0L )
			this.setDirty();
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
			double distanceToUndeadArmy = undeadArmy.getAttackPosition().distSqr( position );

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
			if( undeadArmyToBeSpawned.position.distSqr( position ) < MAXIMUM_DISTANCE_TO_ARMY )
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
				this.undeadArmies.add( new UndeadArmy( this.level, undeadArmyToBeSpawned.position, undeadArmyToBeSpawned.direction ) );
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
	private BlockPos getAttackPosition( Player player ) {
		//Optional< BlockPos > bedPosition = Optional.of( player.blockPosition() );
		/*BlockPos playerPosition = new BlockPos( player.position() );
		BlockPos attackPosition = !bedPosition.isPresent() || playerPosition.distanceSq(
			bedPosition.get() ) >= MAXIMUM_DISTANCE_TO_ARMY ? playerPosition : bedPosition.get();
		*/

		BlockPos playerPosition = player.blockPosition();
		int x = playerPosition.getX(), y = playerPosition.getY(), z = playerPosition.getZ();
		return new BlockPos( x, this.level.getHeight( Heightmap.Types.WORLD_SURFACE, x, z ), z );
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