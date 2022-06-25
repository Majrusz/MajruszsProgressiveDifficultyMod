package com.majruszsdifficulty.gamemodifiers;

import com.majruszsdifficulty.config.GameStageEnumConfig;
import com.majruszsdifficulty.features.undead_army.UndeadArmy;
import com.mlib.Random;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import net.minecraft.world.entity.PathfinderMob;

import static com.majruszsdifficulty.gamemodifiers.CustomConfigs.MobGroups.SIDEKICK_TAG;

public class CustomConditions {
	public static class CRDChance extends Condition {
		final DoubleConfig chance;

		public CRDChance( double defaultChance ) {
			super( Priority.HIGH );
			this.chance = new DoubleConfig( "chance", "Chance of this to happen (this value is scaled by Clamped Regional Difficulty).", false, defaultChance, 0.0, 1.0 );
		}

		@Override
		public void setup( ConfigGroup group ) {
			group.addConfig( this.chance );
		}

		@Override
		public boolean check( GameModifier gameModifier, com.mlib.gamemodifiers.Context.Data data ) {
			return Random.tryChance( com.majruszsdifficulty.GameStage.getRegionalDifficulty( data.entity ) * this.chance.get() );
		}
	}

	public static class GameStage extends Condition {
		final GameStageEnumConfig minimumStage;

		public GameStage( com.majruszsdifficulty.GameStage.Stage minimumStage ) {
			this.minimumStage = new GameStageEnumConfig( "minimum_stage", "Minimum game stage required for that game modifier to happen.", false, minimumStage );
		}

		@Override
		public void setup( ConfigGroup group ) {
			group.addConfig( this.minimumStage );
		}

		@Override
		public boolean check( GameModifier gameModifier, com.mlib.gamemodifiers.Context.Data data ) {
			return com.majruszsdifficulty.GameStage.atLeast( this.minimumStage.get() );
		}
	}

	public static class IsNotSidekick extends Condition {
		public IsNotSidekick() {
			super( Priority.HIGH );
		}

		@Override
		public boolean check( GameModifier gameModifier, com.mlib.gamemodifiers.Context.Data data ) {
			return data.entity instanceof PathfinderMob && !data.entity.getPersistentData().getBoolean( SIDEKICK_TAG );
		}
	}

	public static class IsNotUndeadArmy extends Condition {
		public IsNotUndeadArmy() {
			super( Priority.HIGH );
		}

		@Override
		public boolean check( GameModifier gameModifier, com.mlib.gamemodifiers.Context.Data data ) {
			return !UndeadArmy.doesEntityBelongToUndeadArmy( data.entity );
		}
	}
}
