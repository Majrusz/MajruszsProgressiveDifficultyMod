package com.majruszsdifficulty.gamemodifiers;

import com.majruszsdifficulty.Registries;
import com.mlib.Random;
import com.mlib.config.BooleanConfig;
import com.mlib.config.EnumConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ContextData;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.parameters.Priority;
import net.minecraft.world.entity.PathfinderMob;

import static com.majruszsdifficulty.gamemodifiers.configs.MobGroupConfig.LEADER_TAG;
import static com.majruszsdifficulty.gamemodifiers.configs.MobGroupConfig.SIDEKICK_TAG;

public class CustomConditions {
	/** WARNING: This condition cannot be used with OnSpawned.Context! (use OnSpawned.ContextSafe) */
	public static class CRDChance< DataType extends ContextData > extends Condition.Chance< DataType > {
		final BooleanConfig scaledByCRD;

		public CRDChance( double defaultChance, boolean scaledByCRD ) {
			super( defaultChance );

			this.scaledByCRD = new BooleanConfig( scaledByCRD );

			this.addConfig( this.scaledByCRD.name( "scaled_by_crd" )
				.comment( "Specifies whether the chance should be scaled by Clamped Regional Difficulty." )
			);
		}

		@Override
		public boolean check( GameModifier gameModifier, DataType data ) {
			double multiplier = this.scaledByCRD.isEnabled() ? com.majruszsdifficulty.GameStage.getRegionalDifficulty( data.entity ) : 1.0;

			return Random.tryChance( multiplier * this.chance.getOrDefault() );
		}
	}

	// TODO: ACCEPT LIST OF GAME STAGES
	public static class GameStage< DataType extends ContextData > extends Condition< DataType > {
		final EnumConfig< com.majruszsdifficulty.GameStage > minimumStage;

		public GameStage( com.majruszsdifficulty.GameStage minimumStage ) {
			this.minimumStage = new EnumConfig<>( minimumStage );

			this.addConfig( this.minimumStage.name( "minimum_stage" ).comment( "Minimum game stage required for that to happen." ) );
			this.apply( params->params.configurable( true ) );
		}

		@Override
		public boolean check( GameModifier gameModifier, DataType data ) {
			return com.majruszsdifficulty.GameStage.atLeast( this.minimumStage.get() );
		}
	}

	// TODO: REMOVE
	public static class GameStageExact< DataType extends ContextData > extends Condition< DataType > {
		final com.majruszsdifficulty.GameStage stage;

		public GameStageExact( com.majruszsdifficulty.GameStage stage ) {
			this.stage = stage;
		}

		@Override
		public boolean check( GameModifier gameModifier, ContextData data ) {
			return com.majruszsdifficulty.GameStage.getCurrentStage() == this.stage;
		}
	}

	public static class IsNotPartOfGroup< DataType extends ContextData > extends Condition< DataType > {
		public IsNotPartOfGroup() {
			this.apply( params->params.priority( Priority.HIGH ) );
		}

		@Override
		public boolean check( GameModifier gameModifier, DataType data ) {
			return data.entity instanceof PathfinderMob mob
				&& !mob.getPersistentData().getBoolean( SIDEKICK_TAG )
				&& !mob.getPersistentData().getBoolean( LEADER_TAG );
		}
	}

	public static class IsNotUndeadArmy< DataType extends ContextData > extends Condition< DataType > {
		public IsNotUndeadArmy() {
			this.apply( params->params.priority( Priority.HIGH ) );
		}

		@Override
		public boolean check( GameModifier gameModifier, DataType data ) {
			return !Registries.getUndeadArmyManager().isPartOfUndeadArmy( data.entity );
		}
	}

	public static class IsNotNearUndeadArmy< DataType extends ContextData > extends Condition< DataType > {
		@Override
		public boolean check( GameModifier gameModifier, DataType data ) {
			return data.entity != null
				&& Registries.getUndeadArmyManager().findNearestUndeadArmy( data.entity.blockPosition() ) == null;
		}
	}
}
