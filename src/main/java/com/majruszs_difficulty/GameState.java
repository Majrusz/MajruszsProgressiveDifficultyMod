package com.majruszs_difficulty;

import com.mlib.WorldHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

/** Class representing current game state. On this class depends lot of difficulty improvements. */
public class GameState {
	public static final TextFormatting NORMAL_MODE_COLOR = TextFormatting.WHITE;
	public static final TextFormatting EXPERT_MODE_COLOR = TextFormatting.RED;
	public static final TextFormatting MASTER_MODE_COLOR = TextFormatting.DARK_PURPLE;
	private static State CURRENT = State.NORMAL;

	/** Changing current game state globally. */
	public static boolean changeMode( State state ) {
		if( state == CURRENT )
			return false;

		CURRENT = state;
		return true;
	}

	/** Returning current server game state. */
	public static State getCurrentMode() {
		return CURRENT;
	}

	/** Checking if current state is equal or higher than given state. */
	public static boolean atLeast( State state ) {
		switch( state ) {
			case MASTER:
				return CURRENT == State.MASTER;
			case EXPERT:
				return CURRENT == State.EXPERT || CURRENT == State.MASTER;
			default:
				return true;
		}
	}

	/** Converting game state to integer. */
	public static int convertStateToInteger( State state ) {
		return getValueDependingOnGameState( state, 0, 1, 2 );
	}

	/** Converting integer to game state. */
	public static State convertIntegerToState( int mode ) {
		switch( mode ) {
			default:
				return State.NORMAL;
			case 1:
				return State.EXPERT;
			case 2:
				return State.MASTER;
		}
	}

	/**
	 Returns configuration value depending on given game state.

	 @param state  Game state to test.
	 @param normal Configuration value for Normal game state.
	 @param expert Configuration value for Expert game state.
	 @param master Configuration value for Master game state.
	 */
	public static < ConfigType > ConfigType getValueDependingOnGameState( State state, ConfigType normal, ConfigType expert, ConfigType master ) {
		switch( state ) {
			default:
				return normal;
			case EXPERT:
				return expert;
			case MASTER:
				return master;
		}
	}

	/**
	 Returns configuration value depending on current game state.

	 @param normal Configuration value for Normal game state.
	 @param expert Configuration value for Expert game state.
	 @param master Configuration value for Master game state.
	 */
	public static < ConfigType > ConfigType getValueDependingOnCurrentGameState( ConfigType normal, ConfigType expert, ConfigType master ) {
		return getValueDependingOnGameState( CURRENT, normal, expert, master );
	}

	/** Returns formatted text depending on given game state. */
	public static IFormattableTextComponent getGameStateText( State state ) {
		String modeName = getValueDependingOnGameState( state, "normal", "expert", "master" );
		TextFormatting textColor = getValueDependingOnGameState( state, NORMAL_MODE_COLOR, EXPERT_MODE_COLOR, MASTER_MODE_COLOR );

		return generateModeText( modeName, textColor );
	}

	/** Returns clamped regional difficulty increased by certain value depending on current game state. */
	public static double getRegionalDifficulty( LivingEntity target ) {
		double clampedRegionalDifficulty = target != null ? WorldHelper.getClampedRegionalDifficulty( target ) : 0.25;
		double stateModifier = getStateModifier();

		return MathHelper.clamp( clampedRegionalDifficulty + stateModifier, 0.0, 1.0 );
	}

	/** Returns clamped regional difficulty modifier depending on current game state. */
	public static double getStateModifier() {
		return getValueDependingOnCurrentGameState( 0.0, 0.15, 0.3 );
	}

	/** Returns formatted game state text. */
	private static IFormattableTextComponent generateModeText( String modeName, TextFormatting color ) {
		IFormattableTextComponent text = new TranslationTextComponent( "majruszs_difficulty.states." + modeName );
		text.mergeStyle( color, TextFormatting.BOLD );

		return text;
	}

	/** All possible game states. */
	public static enum State {
		NORMAL, EXPERT, MASTER
	}
}
