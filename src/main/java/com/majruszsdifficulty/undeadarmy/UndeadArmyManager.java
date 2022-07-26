package com.majruszsdifficulty.undeadarmy;

import com.majruszsdifficulty.Registries;
import com.mlib.MajruszLibrary;
import com.mlib.Utility;
import com.mlib.levels.LevelHelper;
import com.mlib.time.TimeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UndeadArmyManager extends SavedData {
	public static final String DATA_NAME = "undead_army";
	static final float MAXIMUM_DISTANCE_TO_ARMY = 12000.0f;
	final List< UndeadArmy > undeadArmies = new ArrayList<>();
	final List< UndeadArmyToSpawn > undeadArmiesToSpawn = new ArrayList<>();
	final ServerLevel level;

	public static boolean isUndeadArmy( @Nullable LivingEntity entity ) {
		return entity != null && !( entity instanceof SkeletonHorse ) && entity.getPersistentData().contains( UndeadArmyKeys.POSITION + "X" );
	}

	public static UndeadArmyManager load( CompoundTag nbt, ServerLevel level ) {
		UndeadArmyManager armyManager = new UndeadArmyManager( level );
		ListTag listNBT = nbt.getList( UndeadArmyKeys.ARMIES, 10 );
		for( int i = 0; i < listNBT.size(); ++i ) {
			armyManager.undeadArmies.add( new UndeadArmy( armyManager.level, listNBT.getCompound( i ) ) );
		}

		return armyManager;
	}

	public UndeadArmyManager( ServerLevel level ) {
		this.level = level;
	}

	@Override
	public CompoundTag save( CompoundTag compoundNBT ) {
		ListTag listNBT = new ListTag();
		this.undeadArmies.forEach( undeadArmy->listNBT.add( undeadArmy.write( new CompoundTag() ) ) );
		compoundNBT.put( UndeadArmyKeys.ARMIES, listNBT );

		return compoundNBT;
	}

	public void tick() {
		this.undeadArmiesToSpawn.forEach( undeadArmyToSpawn->{
			undeadArmyToSpawn.tick();
			if( undeadArmyToSpawn.isReadyToSpawn() ) {
				this.undeadArmies.add( undeadArmyToSpawn.spawn() );
			}
		} );
		this.undeadArmiesToSpawn.removeIf( UndeadArmyToSpawn::isReadyToSpawn );
		this.undeadArmies.forEach( UndeadArmy::tick );
		this.undeadArmies.removeIf( UndeadArmy::hasEnded );
		if( TimeHelper.hasServerSecondsPassed( 10.0 ) ) {
			this.setDirty();
		}
	}

	public boolean tryToSpawn( Player player ) {
		return LevelHelper.isEntityIn( player, Level.OVERWORLD ) && tryToSpawn( getAttackPosition( player ) );
	}

	public boolean tryToSpawn( BlockPos attackPosition ) {
		return tryToSpawn( attackPosition, Optional.empty() );
	}

	public boolean tryToSpawn( BlockPos attackPosition, Optional< Direction > optionalDirection ) {
		if( findNearestUndeadArmy( attackPosition ) != null || isArmySpawningHere( attackPosition ) || !UndeadArmyConfig.isEnabled() )
			return false;

		Direction direction = optionalDirection.orElseGet( Direction::getRandom );
		this.undeadArmiesToSpawn.add( new UndeadArmyToSpawn( this.level, attackPosition, direction ) );
		this.level.playSound( null, attackPosition, Registries.UNDEAD_ARMY_APPROACHING.get(), SoundSource.AMBIENT, 0.25f, 1.0f );
		MajruszLibrary.LOGGER.info( "Undead Army started at " + attackPosition + "!" );

		return true;
	}

	@Nullable
	public UndeadArmy findNearestUndeadArmy( BlockPos position ) {
		UndeadArmy nearestArmy = null;
		double minimumDistance = MAXIMUM_DISTANCE_TO_ARMY;
		for( UndeadArmy undeadArmy : this.undeadArmies ) {
			double distanceToUndeadArmy = position.distSqr( undeadArmy.getAttackedPosition() );
			if( undeadArmy.isActive() && distanceToUndeadArmy < minimumDistance ) {
				nearestArmy = undeadArmy;
				minimumDistance = distanceToUndeadArmy;
			}
		}

		return nearestArmy;
	}

	private boolean isArmySpawningHere( BlockPos position ) {
		for( UndeadArmyToSpawn undeadArmyToSpawn : this.undeadArmiesToSpawn )
			if( undeadArmyToSpawn.isOccupied( position ) )
				return true;

		return false;
	}

	private BlockPos getAttackPosition( Player player ) {
		BlockPos playerPosition = player.blockPosition();
		int x = playerPosition.getX(), z = playerPosition.getZ();
		return new BlockPos( x, this.level.getHeight( Heightmap.Types.WORLD_SURFACE, x, z ), z );
	}

	public static class UndeadArmyToSpawn {
		final ServerLevel level;
		final BlockPos position;
		final Direction direction;
		int ticksToSpawn;

		public UndeadArmyToSpawn( ServerLevel level, BlockPos position, Direction direction ) {
			this.level = level;
			this.position = position;
			this.direction = direction;
			this.ticksToSpawn = Utility.secondsToTicks( 6.5 );
		}

		public UndeadArmy spawn() {
			return new UndeadArmy( this.level, this.position, this.direction );
		}

		public boolean isOccupied( BlockPos position ) {
			return this.position.distSqr( position ) < MAXIMUM_DISTANCE_TO_ARMY;
		}

		public void tick() {
			--this.ticksToSpawn;
		}

		public boolean isReadyToSpawn() {
			return this.ticksToSpawn <= 0;
		}
	}
}
