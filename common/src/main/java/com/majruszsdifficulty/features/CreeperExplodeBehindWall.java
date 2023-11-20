package com.majruszsdifficulty.features;

import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import com.majruszlibrary.contexts.OnEntitySpawned;
import com.majruszlibrary.contexts.base.Condition;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.math.Range;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class CreeperExplodeBehindWall {
	private static final GameStageValue< Boolean > IS_ENABLED = GameStageValue.disabledOn( GameStage.NORMAL_ID );
	private static float CHANCE = 1.0f;
	private static boolean IS_SCALED_BY_CRD = false;

	static {
		OnEntitySpawned.listen( CreeperExplodeBehindWall::modifyAI )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.chanceCRD( ()->CHANCE, ()->IS_SCALED_BY_CRD ) )
			.addCondition( data->IS_ENABLED.get( GameStageHelper.determineGameStage( data ) ) )
			.addCondition( data->data.entity instanceof Creeper );

		Serializables.getStatic( Config.Features.class )
			.define( "creeper_explode_behind_wall", CreeperExplodeBehindWall.class );

		Serializables.getStatic( CreeperExplodeBehindWall.class )
			.define( "is_enabled", Reader.map( Reader.bool() ), ()->IS_ENABLED.get(), v->IS_ENABLED.set( v ) )
			.define( "chance", Reader.number(), ()->CHANCE, v->CHANCE = Range.CHANCE.clamp( v ) )
			.define( "is_scaled_by_crd", Reader.bool(), ()->IS_SCALED_BY_CRD, v->IS_SCALED_BY_CRD = v );
	}

	private static void modifyAI( OnEntitySpawned data ) {
		Creeper creeper = ( ( Creeper )data.entity );

		EntityHelper.getGoalSelector( creeper ).addGoal( 1, new ExplodeBehindWallGoal( creeper ) );
	}

	public static class ExplodeBehindWallGoal extends Goal {
		private static final double MAXIMUM_START_DISTANCE = 19.0;
		private static final double MAXIMUM_EXPLODE_DISTANCE = 49.0;
		private static final double OFFSET = 16.0;
		private final Creeper creeper;
		private LivingEntity attackTarget;

		public ExplodeBehindWallGoal( Creeper creeper ) {
			this.creeper = creeper;
			this.setFlags( EnumSet.of( Goal.Flag.MOVE ) );
		}

		@Override
		public boolean canUse() {
			LivingEntity target = this.getNearestPlayer( this.creeper );

			return this.creeper.getSwellDir() > 0
				|| target != null
				&& this.creeper.distanceToSqr( target ) < MAXIMUM_START_DISTANCE * getDistanceMultiplier();
		}

		@Override
		public void start() {
			this.creeper.getNavigation().stop();
			this.attackTarget = this.getNearestPlayer( this.creeper );
		}

		@Override
		public void stop() {
			this.attackTarget = null;
		}

		@Override
		public void tick() {
			if( this.attackTarget == null || this.creeper.distanceToSqr( this.attackTarget ) > MAXIMUM_EXPLODE_DISTANCE * this.getDistanceMultiplier() ) {
				this.creeper.setSwellDir( -1 ); // stops creeper's explosion
			} else {
				this.creeper.setSwellDir( 1 );
			}
		}

		private double getDistanceMultiplier() {
			double sizeMultiplier = 1.0; // TODO: this.creeper instanceof CreeperlingEntity ? 0.6 : 1.0;
			double chargedMultiplier = this.creeper.isPowered() ? 2.0 : 1.0;

			return sizeMultiplier * chargedMultiplier;
		}

		private @Nullable Player getNearestPlayer( Creeper creeper ) {
			if( !( creeper.level() instanceof ServerLevel level ) ) {
				return null;
			}

			Player nearestPlayer = null;
			for( Player player : EntityHelper.getEntitiesNearby( Player.class, level, creeper.position(), OFFSET ) ) {
				if( EntityHelper.isOnCreativeMode( player ) || EntityHelper.isOnSpectatorMode( player ) ) {
					continue;
				}

				if( nearestPlayer == null || creeper.distanceToSqr( player ) < creeper.distanceToSqr( nearestPlayer ) ) {
					nearestPlayer = player;
				}
			}

			return nearestPlayer;
		}
	}
}
