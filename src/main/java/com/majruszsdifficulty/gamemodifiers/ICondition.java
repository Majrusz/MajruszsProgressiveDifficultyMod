package com.majruszsdifficulty.gamemodifiers;

import com.majruszsdifficulty.config.GameStageEnumConfig;
import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import net.minecraft.client.Minecraft;

import java.util.function.Predicate;

public interface ICondition {
	boolean check( GameModifier feature, Object data );

	default void setupConfig( ConfigGroup config ) {}

	class Excludable implements ICondition {
		final AvailabilityConfig availability;

		public Excludable() {
			this.availability = new AvailabilityConfig( "is_enabled", "Specifies whether this game modifier is enabled.", false, true );
		}

		@Override
		public void setupConfig( ConfigGroup config ) {
			config.addConfig( this.availability );
		}

		@Override
		public boolean check( GameModifier gameModifier, Object data ) {
			return this.availability.isEnabled();
		}
	}

	class Chance implements ICondition {
		final DoubleConfig chance;
		final boolean multiplicableByCRD;

		public Chance( double defaultChance, boolean multiplicableByCRD ) {
			String comment = String.format( "Chance of this to happen%s.", multiplicableByCRD ? " (this value is scaled by Clamped Regional Difficulty)" : "" );
			this.chance = new DoubleConfig( "chance", comment, false, defaultChance, 0.0, 1.0 );
			this.multiplicableByCRD = multiplicableByCRD;
		}

		@Override
		public void setupConfig( ConfigGroup config ) {
			config.addConfig( this.chance );
		}

		@Override
		public boolean check( GameModifier gameModifier, Object data ) {
			double clampedRegionalDifficulty = com.majruszsdifficulty.GameStage.getRegionalDifficulty( Minecraft.getInstance().player );

			return Random.tryChance( ( this.multiplicableByCRD ? clampedRegionalDifficulty : 1.0 ) * this.chance.get() );
		}
	}

	class GameStage implements ICondition {
		final GameStageEnumConfig minimumStage;

		public GameStage( com.majruszsdifficulty.GameStage.Stage minimumStage ) {
			this.minimumStage = new GameStageEnumConfig( "minimum_stage", "Minimum required game stage for that game modifier to happen.", false, minimumStage );
		}

		@Override
		public void setupConfig( ConfigGroup config ) {
			config.addConfig( this.minimumStage );
		}

		@Override
		public boolean check( GameModifier gameModifier, Object data ) {
			return com.majruszsdifficulty.GameStage.atLeast( this.minimumStage.get() );
		}
	}

	class Context < DataType extends com.majruszsdifficulty.gamemodifiers.Context.Data > implements ICondition {
		final Class< DataType > dataClass;
		final Predicate< DataType > predicate;

		public Context( Class< DataType > dataClass, Predicate< DataType > predicate ) {
			this.dataClass = dataClass;
			this.predicate = predicate;
		}

		@Override
		public boolean check( GameModifier gameModifier, Object data ) {
			DataType contextData = Utility.castIfPossible( this.dataClass, data );
			assert contextData != null;

			return this.predicate.test( contextData );
		}
	}
}
