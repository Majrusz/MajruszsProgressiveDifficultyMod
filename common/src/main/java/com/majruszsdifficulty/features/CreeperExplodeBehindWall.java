package com.majruszsdifficulty.features;

import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnEntitySpawned;
import com.mlib.contexts.base.Condition;
import com.mlib.data.Serializables;
import com.mlib.entity.EntityHelper;
import com.mlib.math.Range;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

@AutoInstance
public class CreeperExplodeBehindWall {
	private GameStageValue< Boolean > isEnabled = GameStageValue.disabledOn( GameStage.NORMAL_ID );
	private float chance = 1.0f;
	private boolean isScaledByCRD = false;

	public CreeperExplodeBehindWall() {
		OnEntitySpawned.listen( this::modifyAI )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.chanceCRD( ()->this.chance, ()->this.isScaledByCRD ) )
			.addCondition( data->this.isEnabled.get( GameStageHelper.determineGameStage( data ) ) )
			.addCondition( data->data.entity instanceof Creeper );

		Serializables.get( Config.Features.class )
			.define( "creeper_explode_behind_wall", subconfig->{
				subconfig.defineBooleanMap( "is_enabled", s->this.isEnabled.get(), ( s, v )->this.isEnabled.set( v ) );
				subconfig.defineFloat( "chance", s->this.chance, ( s, v )->this.chance = Range.CHANCE.clamp( v ) );
				subconfig.defineBoolean( "is_scaled_by_crd", s->this.isScaledByCRD, ( s, v )->this.isScaledByCRD = v );
			} );
	}

	private void modifyAI( OnEntitySpawned data ) {
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
