package com.majruszsdifficulty.undeadarmy;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UndeadArmyManager extends SavedData {
	public static final UndeadArmyManager NOT_LOADED = new UndeadArmyManager( null, null );
	final List< UndeadArmy > undeadArmies = new ArrayList<>();
	final ServerLevel level;
	final Config config;

	public UndeadArmyManager( ServerLevel level, Config config ) {
		this.level = level;
		this.config = config;
	}

	public UndeadArmyManager( ServerLevel level, Config config, CompoundTag nbt ) {
		this( level, config );

		ListTag tags = nbt.getList( Keys.ARMIES, 10 );
		for( int i = 0; i < tags.size(); ++i ) {
			this.undeadArmies.add( new UndeadArmy( level, config, new UndeadArmyData( tags.getCompound( i ) ) ) );
		}
	}

	@Override
	public CompoundTag save( CompoundTag nbt ) {
		ListTag tags = new ListTag();
		this.undeadArmies.forEach( undeadArmy->{
			CompoundTag tag = new CompoundTag();
			undeadArmy.getData().write( tag );

			tags.add( tag );
		} );
		nbt.put( Keys.ARMIES, tags );

		return nbt;
	}

	public boolean tryToSpawn( BlockPos position, Optional< Direction > direction ) {
		this.undeadArmies.add( new UndeadArmy( this.level, this.config, new UndeadArmyData( position, direction.orElseGet( Direction::getRandom ) ) ) );

		return true;
	}

	@Nullable
	public UndeadArmy findNearestUndeadArmy( BlockPos position ) {
		UndeadArmy nearestArmy = null;
		double minDistance = Double.MAX_VALUE;
		for( UndeadArmy undeadArmy : this.undeadArmies ) {
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

	void tick() {
		this.undeadArmies.forEach( UndeadArmy::tick );
		boolean hasAnyArmyFinished = this.undeadArmies.removeIf( UndeadArmy::hasFinished );
	}
}
