package com.majruszsdifficulty.undeadarmy;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.events.OnLevelsLoaded;
import com.majruszlibrary.events.OnServerTicked;
import com.majruszlibrary.level.LevelHelper;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.platform.Side;
import com.majruszlibrary.time.TimeHelper;
import com.majruszsdifficulty.data.WorldData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UndeadArmyHelper {
	private static List< UndeadArmy > UNDEAD_ARMIES = new ArrayList<>();

	static {
		OnLevelsLoaded.listen( UndeadArmyHelper::setupDefaultValues );

		OnServerTicked.listen( UndeadArmyHelper::tick );

		Serializables.getStatic( WorldData.class )
			.define( "undead_armies", Reader.list( Reader.custom( UndeadArmy::new ) ), ()->UNDEAD_ARMIES, v->UNDEAD_ARMIES = v );
	}

	public static boolean tryToSpawn( Player player ) {
		return EntityHelper.isOutside( player )
			&& EntityHelper.isIn( player, Level.OVERWORLD )
			&& UndeadArmyHelper.tryToSpawn( UndeadArmyHelper.getAttackPosition( player ), Optional.empty() );
	}

	public static boolean tryToSpawn( BlockPos position, Optional< UndeadArmy.Direction > direction ) {
		return UndeadArmyConfig.IS_ENABLED
			&& UndeadArmyHelper.getLevel().getDifficulty() != Difficulty.PEACEFUL
			&& !UndeadArmyHelper.getLevel().getGameRules().getBoolean( GameRules.RULE_DISABLE_RAIDS )
			&& UndeadArmyHelper.findNearestUndeadArmy( position ) == null
			&& UndeadArmyHelper.setupNewArmy( position, direction );
	}

	public static @Nullable UndeadArmy findNearestUndeadArmy( BlockPos position ) {
		UndeadArmy nearestArmy = null;
		double minDistance = Double.MAX_VALUE;
		for( UndeadArmy undeadArmy : UNDEAD_ARMIES ) {
			if( !undeadArmy.isInRange( position ) ) {
				continue;
			}

			double distance = undeadArmy.distanceTo( position );
			if( distance < minDistance ) {
				nearestArmy = undeadArmy;
				minDistance = distance;
			}
		}

		return nearestArmy;
	}

	public static boolean isPartOfUndeadArmy( Entity entity ) {
		return UNDEAD_ARMIES.stream().anyMatch( undeadArmy->undeadArmy.isPartOfWave( entity ) );
	}

	public static List< UndeadArmy > getUndeadArmies() {
		return Collections.unmodifiableList( UNDEAD_ARMIES );
	}

	public static ServerLevel getLevel() {
		return Side.getServer().overworld();
	}

	private static void setupDefaultValues( OnLevelsLoaded data ) {
		UNDEAD_ARMIES = new ArrayList<>();
	}

	private static void tick( OnServerTicked data ) {
		UNDEAD_ARMIES.forEach( UndeadArmy::tick );
		boolean hasAnyArmyFinished = UNDEAD_ARMIES.removeIf( UndeadArmy::hasFinished );
		if( hasAnyArmyFinished && UNDEAD_ARMIES.isEmpty() ) {
			LevelHelper.setClearWeather( UndeadArmyHelper.getLevel(), TimeHelper.toTicks( 0.5 ) );
		}
	}

	private static boolean setupNewArmy( BlockPos position, Optional< UndeadArmy.Direction > direction ) {
		UndeadArmy undeadArmy = new UndeadArmy();
		undeadArmy.start( position, direction.orElse( Random.next( UndeadArmy.Direction.values() ) ) );
		UNDEAD_ARMIES.add( undeadArmy );

		return true;
	}

	private static BlockPos getAttackPosition( Player player ) {
		int x = ( int )player.getX();
		int z = ( int )player.getZ();

		return new BlockPos( x, player.level().getHeight( Heightmap.Types.WORLD_SURFACE, x, z ), z );
	}
}
