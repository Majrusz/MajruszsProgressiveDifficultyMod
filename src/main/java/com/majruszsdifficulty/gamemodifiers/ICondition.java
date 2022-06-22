package com.majruszsdifficulty.gamemodifiers;

import com.majruszsdifficulty.config.GameStageEnumConfig;
import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.config.BooleanConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.entities.EntityHelper;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public interface ICondition {
	boolean check( GameModifier feature, com.majruszsdifficulty.gamemodifiers.Context.Data data );

	default void setup( ConfigGroup group ) {}

	class Excludable implements ICondition {
		final BooleanConfig availability;

		public Excludable() {
			this.availability = new BooleanConfig( "is_enabled", "Specifies whether this game modifier is enabled.", false, true );
		}

		@Override
		public void setup( ConfigGroup group ) {
			group.addConfig( this.availability );
		}

		@Override
		public boolean check( GameModifier gameModifier, com.majruszsdifficulty.gamemodifiers.Context.Data data ) {
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
		public void setup( ConfigGroup group ) {
			group.addConfig( this.chance );
		}

		@Override
		public boolean check( GameModifier gameModifier, com.majruszsdifficulty.gamemodifiers.Context.Data data ) {
			double clampedRegionalDifficulty = com.majruszsdifficulty.GameStage.getRegionalDifficulty( data.entity );

			return Random.tryChance( ( this.multiplicableByCRD ? clampedRegionalDifficulty : 1.0 ) * this.chance.get() );
		}
	}

	class GameStage implements ICondition {
		final GameStageEnumConfig minimumStage;

		public GameStage( com.majruszsdifficulty.GameStage.Stage minimumStage ) {
			this.minimumStage = new GameStageEnumConfig( "minimum_stage", "Minimum game stage required for that game modifier to happen.", false, minimumStage );
		}

		@Override
		public void setup( ConfigGroup group ) {
			group.addConfig( this.minimumStage );
		}

		@Override
		public boolean check( GameModifier gameModifier, com.majruszsdifficulty.gamemodifiers.Context.Data data ) {
			return com.majruszsdifficulty.GameStage.atLeast( this.minimumStage.get() );
		}
	}

	class Context< DataType extends com.majruszsdifficulty.gamemodifiers.Context.Data > implements ICondition {
		final Class< DataType > dataClass;
		final Predicate< DataType > predicate;

		public Context( Class< DataType > dataClass, Predicate< DataType > predicate ) {
			this.dataClass = dataClass;
			this.predicate = predicate;
		}

		@Override
		public boolean check( GameModifier gameModifier, com.majruszsdifficulty.gamemodifiers.Context.Data data ) {
			DataType contextData = Utility.castIfPossible( this.dataClass, data );
			assert contextData != null;

			return this.predicate.test( contextData );
		}
	}

	class IsLivingBeing implements ICondition {
		@Override
		public boolean check( GameModifier feature, com.majruszsdifficulty.gamemodifiers.Context.Data data ) {
			return EntityHelper.isAnimal( data.entity ) || EntityHelper.isHuman( data.entity );
		}
	}

	class ArmorDependentChance implements ICondition {
		@Override
		public boolean check( GameModifier feature, com.majruszsdifficulty.gamemodifiers.Context.Data data ) {
			return Random.tryChance( getChance( data.entity ) );
		}

		private double getChance( @Nullable LivingEntity entity ) {
			if( entity == null )
				return 1.0;

			MutableInt armorCount = new MutableInt( 0 );
			entity.getArmorSlots().forEach( itemStack->{
				if( !itemStack.isEmpty() )
					armorCount.add( 1 );
			} );
			return switch( armorCount.getValue() ) {
				default -> 1.0;
				case 1 -> 0.7;
				case 2 -> 0.49;
				case 3 -> 0.34;
				case 4 -> 0.24;
			};
		}
	}
}
