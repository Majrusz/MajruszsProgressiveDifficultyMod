package com.majruszsdifficulty.gamemodifiers;

import com.majruszsdifficulty.undeadarmy.UndeadArmyManager;
import com.mlib.Random;
import com.mlib.config.BooleanConfig;
import com.mlib.config.DoubleConfig;
import com.mlib.config.EnumConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ContextData;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.parameters.Priority;
import com.mlib.math.AABBHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;

import static com.majruszsdifficulty.gamemodifiers.configs.MobGroupConfig.SIDEKICK_TAG;

public class CustomConditions {
	public static class CRDChance extends Condition {
		final DoubleConfig chance;
		final BooleanConfig scaledByCRD;

		public CRDChance( double defaultChance, boolean scaledByCRD ) {
			super( Priority.HIGH );
			this.chance = new DoubleConfig( "chance", "Chance of this to happen.", false, defaultChance, 0.0, 1.0 );
			this.scaledByCRD = new BooleanConfig( "scaled_by_crd", "Specifies whether the chance should be scaled by Clamped Regional Difficulty.", false, scaledByCRD );
			this.addConfigs( this.chance, this.scaledByCRD );
		}

		@Override
		public boolean check( GameModifier gameModifier, ContextData data ) {
			double multiplier = this.scaledByCRD.isEnabled() ? com.majruszsdifficulty.GameStage.getRegionalDifficulty( data.entity ) : 1.0;

			return Random.tryChance( multiplier * this.chance.get() );
		}
	}

	public static class GameStage extends Condition {
		final EnumConfig< com.majruszsdifficulty.GameStage.Stage > minimumStage;

		public GameStage( com.majruszsdifficulty.GameStage.Stage minimumStage ) {
			this.minimumStage = new EnumConfig<>( "minimum_stage", "Minimum game stage required for that to happen.", false, minimumStage );
			this.addConfig( this.minimumStage );
		}

		@Override
		public boolean check( GameModifier gameModifier, ContextData data ) {
			return com.majruszsdifficulty.GameStage.atLeast( this.minimumStage.get() );
		}
	}

	public static class IsNotSidekick extends Condition {
		public IsNotSidekick() {
			super( Priority.HIGH );
		}

		@Override
		public boolean check( GameModifier gameModifier, ContextData data ) {
			return data.entity instanceof PathfinderMob && !data.entity.getPersistentData().getBoolean( SIDEKICK_TAG );
		}
	}

	public static class IsNotUndeadArmy extends Condition {
		public IsNotUndeadArmy() {
			super( Priority.HIGH );
		}

		@Override
		public boolean check( GameModifier gameModifier, ContextData data ) {
			return !UndeadArmyManager.isUndeadArmy( data.entity );
		}
	}

	public static class IsNotTooManyMobsNearby extends Condition {
		public IsNotTooManyMobsNearby() {
			super( Priority.LOWEST ); // it can significantly affect the performance
		}

		@Override
		public boolean check( GameModifier gameModifier, ContextData data ) {
			if( data.level == null || data.entity == null ) {
				return false;
			}

			return data.level.getEntities( data.entity, AABBHelper.createInflatedAABB( data.entity.position(), 10.0 ), entity->entity instanceof LivingEntity )
				.size() < 15;
		}
	}
}
