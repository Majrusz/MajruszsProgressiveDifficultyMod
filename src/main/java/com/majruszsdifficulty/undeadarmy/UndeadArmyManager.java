package com.majruszsdifficulty.undeadarmy;

import com.majruszsdifficulty.undeadarmy.data.Direction;
import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.data.SerializableStructure;
import com.mlib.levels.LevelHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UndeadArmyManager extends SerializableStructure {
	public static final UndeadArmyManager NOT_LOADED = new UndeadArmyManager();
	final ServerLevel level;
	final Config config;
	List< UndeadArmy > undeadArmies = new ArrayList<>();

	public UndeadArmyManager( ServerLevel level, Config config ) {
		this.level = level;
		this.config = config;

		this.defineCustom( "undead_armies", ()->this.undeadArmies, x->this.undeadArmies = x, ()->new UndeadArmy( level, config ) );
	}

	private UndeadArmyManager() {
		this.level = null;
		this.config = null;
	}

	public boolean tryToSpawn( Player player ) {
		return LevelHelper.isEntityOutside( player )
			&& LevelHelper.isEntityIn( player, Level.OVERWORLD )
			&& this.tryToSpawn( this.getAttackPosition( player ), Optional.empty() );
	}

	public boolean tryToSpawn( BlockPos position, Optional< Direction > direction ) {
		return this.config.isEnabled()
			&& this.level.getDifficulty() != Difficulty.PEACEFUL
			&& !this.level.getGameRules().getBoolean( GameRules.RULE_DISABLE_RAIDS )
			&& this.findNearestUndeadArmy( position ) == null
			&& this.undeadArmies.add( this.setupNewArmy( position, direction ) );
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

	public boolean isPartOfUndeadArmy( Entity entity ) {
		return this.undeadArmies.stream().anyMatch( undeadArmy->undeadArmy.isPartOfWave( entity ) );
	}

	public List< UndeadArmy > getUndeadArmies() {
		return Collections.unmodifiableList( this.undeadArmies );
	}

	public Config getConfig() {
		return this.config;
	}

	void tick() {
		this.undeadArmies.forEach( UndeadArmy::tick );
		boolean hasAnyArmyFinished = this.undeadArmies.removeIf( UndeadArmy::hasFinished );
		if( hasAnyArmyFinished && this.undeadArmies.isEmpty() ) {
			LevelHelper.setClearWeather( this.level, Utility.minutesToTicks( 0.5 ) );
		}
	}

	private UndeadArmy setupNewArmy( BlockPos position, Optional< Direction > direction ) {
		UndeadArmy undeadArmy = new UndeadArmy( this.level, this.config );
		undeadArmy.start( position, direction.orElse( Random.nextRandom( Direction.values() ) ) );

		return undeadArmy;
	}

	private BlockPos getAttackPosition( Player player ) {
		int x = ( int )player.getX();
		int z = ( int )player.getZ();

		return new BlockPos( x, this.level.getHeight( Heightmap.Types.WORLD_SURFACE, x, z ), z );
	}
}
