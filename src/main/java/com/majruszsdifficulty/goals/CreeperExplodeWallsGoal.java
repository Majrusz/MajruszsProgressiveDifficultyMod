package com.majruszsdifficulty.goals;

import com.majruszsdifficulty.entities.CreeperlingEntity;
import com.mlib.Utility;
import com.mlib.entities.EntityHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

/** Makes the creeper explode if there is a player behind the wall. */
public class CreeperExplodeWallsGoal extends Goal {
	private static final double MAXIMUM_START_DISTANCE = 19.0;
	private static final double MAXIMUM_EXPLODE_DISTANCE = 49.0;
	private static final double OFFSET = 16.0;
	private final Creeper creeper;
	private LivingEntity attackTarget;

	public CreeperExplodeWallsGoal( Creeper creeper ) {
		this.creeper = creeper;
		this.setFlags( EnumSet.of( Goal.Flag.MOVE ) );
	}

	/** Returns whether execution should begin. */
	@Override
	public boolean canUse() {
		LivingEntity target = getNearestPlayer( this.creeper );

		return this.creeper.getSwellDir() > 0 || target != null && this.creeper.distanceToSqr( target ) < MAXIMUM_START_DISTANCE * getDistanceMultiplier();
	}

	/** Executes task at the beginning of goal. */
	@Override
	public void start() {
		this.creeper.getNavigation().stop();
		this.attackTarget = getNearestPlayer( this.creeper );
	}

	/** Resets the task's internal state. */
	@Override
	public void stop() {
		this.attackTarget = null;
	}

	/** Updates state of goal each tick. */
	@Override
	public void tick() {
		if( this.attackTarget == null || this.creeper.distanceToSqr( this.attackTarget ) > MAXIMUM_EXPLODE_DISTANCE * getDistanceMultiplier() ) {
			this.creeper.setSwellDir( -1 ); // stops creeper's explosion
		} else {
			this.creeper.setSwellDir( 1 );
		}
	}

	/** Returns distance multiplier. */
	private double getDistanceMultiplier() {
		double sizeMultiplier = this.creeper instanceof CreeperlingEntity ? 0.6 : 1.0;
		double chargedMultiplier = this.creeper.isPowered() ? 2.0 : 1.0;

		return sizeMultiplier * chargedMultiplier;
	}

	/** Returns the nearest player. */
	@Nullable
	private Player getNearestPlayer( Creeper creeper ) {
		ServerLevel level = Utility.castIfPossible( ServerLevel.class, creeper.level );
		if( level == null )
			return null;

		Predicate< Player > playerPredicate = player->!EntityHelper.isOnCreativeMode( player );
		List< Player > nearestPlayers = EntityHelper.getEntitiesInSphere( Player.class, level, creeper, OFFSET, playerPredicate );

		Player nearestPlayer = null;
		for( Player player : nearestPlayers )
			if( nearestPlayer == null || creeper.distanceToSqr( player ) < creeper.distanceToSqr( nearestPlayer ) )
				nearestPlayer = player;

		return nearestPlayer;
	}
}

