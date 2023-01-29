package com.majruszsdifficulty.undeadarmy;

import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.data.SerializableStructure;
import com.mlib.levels.LevelHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class UndeadArmyManager extends SavedData {
	public static final UndeadArmyManager NOT_LOADED = new UndeadArmyManager( null, null );
	final UndeadArmies undeadArmies;
	final ServerLevel level;
	final Config config;

	public UndeadArmyManager( ServerLevel level, Config config ) {
		this.level = level;
		this.config = config;
		this.undeadArmies = new UndeadArmies( level, config );
	}

	public UndeadArmyManager( ServerLevel level, Config config, CompoundTag nbt ) {
		this( level, config );

		this.undeadArmies.read( nbt );
	}

	@Override
	public CompoundTag save( CompoundTag nbt ) {
		this.undeadArmies.write( nbt );

		return nbt;
	}

	public boolean tryToSpawn( BlockPos position, Optional< Direction > direction ) {
		UndeadArmy undeadArmy = new UndeadArmy( this.level, this.config );
		undeadArmy.start( position, direction.orElse( Random.nextRandom( Direction.values() ) ) );

		return this.undeadArmies.add( undeadArmy );
	}

	@Nullable
	public UndeadArmy findNearestUndeadArmy( BlockPos position ) {
		UndeadArmy nearestArmy = null;
		double minDistance = Double.MAX_VALUE;
		for( UndeadArmy undeadArmy : this.undeadArmies.get() ) {
			if( !undeadArmy.isInRange( position ) )
				continue;

			double distance = undeadArmy.distanceTo( position );
			if( distance < minDistance ) {
				nearestArmy = undeadArmy;
				minDistance = distance;
			}
		}

		return nearestArmy;
	}

	public boolean isPartOfUndeadArmy( Entity entity ) {
		return this.undeadArmies.get().stream().anyMatch( undeadArmy->undeadArmy.isPartOfWave( entity ) );
	}

	void tick() {
		this.undeadArmies.forEach( UndeadArmy::tick );
		boolean hasAnyArmyFinished = this.undeadArmies.removeIf( UndeadArmy::hasFinished );
		if( hasAnyArmyFinished && this.undeadArmies.get().isEmpty() ) {
			LevelHelper.setClearWeather( this.level, Utility.minutesToTicks( 0.5 ) );
		}
	}

	static class UndeadArmies extends SerializableStructure {
		private final List< UndeadArmy > undeadArmies = new ArrayList<>();

		public UndeadArmies( ServerLevel level, Config config ) {
			this.define( "undead_armies", ()->this.undeadArmies, this.undeadArmies::addAll, ()->new UndeadArmy( level, config ) );
		}

		public boolean add( UndeadArmy undeadArmy ) {
			return this.undeadArmies.add( undeadArmy );
		}

		public void forEach( Consumer< UndeadArmy > consumer ) {
			this.undeadArmies.forEach( consumer );
		}

		public boolean removeIf( Predicate< UndeadArmy > predicate ) {
			return this.undeadArmies.removeIf( predicate );
		}

		public List< UndeadArmy > get() {
			return this.undeadArmies;
		}
	}
}
