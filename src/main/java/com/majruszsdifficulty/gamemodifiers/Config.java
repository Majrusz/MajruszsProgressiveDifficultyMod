package com.majruszsdifficulty.gamemodifiers;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.config.GameStageDoubleConfig;
import com.majruszsdifficulty.config.GameStageIntegerConfig;
import com.mlib.Utility;
import com.mlib.config.ConfigGroup;

public abstract class Config {
	final String groupName;
	final String groupComment;

	public Config( String groupName, String groupComment ) {
		this.groupName = groupName;
		this.groupComment = groupComment;
	}

	public abstract void setup( ConfigGroup group );

	public ConfigGroup addNewGroup( ConfigGroup group ) {
		return group.addNewGroup( this.groupName, this.groupComment );
	}

	public static class Effect extends Config {
		static final int MIN_AMPLIFIER = 0, MAX_AMPLIFIER = 10;
		static final double MIN_DURATION = 1.0, MAX_DURATION = 99.0;
		final GameStageIntegerConfig amplifier;
		final GameStageDoubleConfig duration;

		public Effect( String groupName, GameStage.Integer amplifier, GameStage.Double duration ) {
			super( groupName, "" );
			this.amplifier = new GameStageIntegerConfig( "Amplifier", "Level of the effect to apply.", amplifier.normal(), amplifier.expert(), amplifier.master(), MIN_AMPLIFIER, MAX_AMPLIFIER );
			this.duration = new GameStageDoubleConfig( "Duration", "Duration in seconds.", duration.normal(), duration.expert(), duration.master(), MIN_DURATION, MAX_DURATION );
		}

		public Effect( String groupName, int amplifier, GameStage.Double duration ) {
			this( groupName, new GameStage.Integer( amplifier, amplifier, amplifier ), duration );
		}

		public Effect( String groupName, GameStage.Integer amplifier, double duration ) {
			this( groupName, amplifier, new GameStage.Double( duration, duration, duration ) );
		}

		public Effect( String groupName, int amplifier, double duration ) {
			this( groupName, new GameStage.Integer( amplifier, amplifier, amplifier ), new GameStage.Double( duration, duration, duration ) );
		}

		public int getAmplifier() {
			return this.amplifier.getCurrentGameStageValue();
		}

		public int getDuration() {
			return Utility.secondsToTicks( this.duration.getCurrentGameStageValue() );
		}

		@Override
		public void setup( ConfigGroup group ) {
			group.addConfigs( this.amplifier, this.duration );
		}
	}

	public static class Bleeding extends Effect {
		public Bleeding( GameStage.Double duration ) {
			super( "Bleeding", new GameStage.Integer( 0, 1, 2 ), duration );
		}

		public Bleeding( double duration ) {
			this( new GameStage.Double( duration, duration, duration ) );
		}

		public Bleeding() {
			this( 24.0 );
		}
	}
}
