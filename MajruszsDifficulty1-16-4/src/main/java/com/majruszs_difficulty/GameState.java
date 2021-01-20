package com.majruszs_difficulty;

import com.majruszs_difficulty.ConfigHandlerOld.Config;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

/** Class representing current game state. On this class depends lot of difficulty improvements. */
public class GameState {
	public static final TextFormatting NORMAL_MODE_COLOR = TextFormatting.WHITE;
	public static final TextFormatting EXPERT_MODE_COLOR = TextFormatting.RED;
	public static final TextFormatting MASTER_MODE_COLOR = TextFormatting.DARK_PURPLE;
	private static State current = State.NORMAL;

	/** Changing current game state globally. */
	public static boolean changeMode( State state ) {
		if( state == current )
			return false;

		current = state;
		return true;
	}

	/** Returning current server game state. */
	public static State getCurrentMode() {
		return current;
	}

	/** Checking if current state is equal or higher than given state. */
	public static boolean atLeast( State state ) {
		if( state == State.EXPERT ) {
			return ( current == State.EXPERT || current == State.MASTER );
		} else if( state == State.MASTER ) {
			return current == State.MASTER;
		} else
			return true;
	}

	/** Converting game state to integer. */
	public static int convertStateToInteger( State state ) {
		switch( state ) {
			default:
				return 0;
			case EXPERT:
				return 1;
			case MASTER:
				return 2;
		}
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
	 Returns integer depending on current game state.

	 @param normal Configuration for Normal game state.
	 @param expert Configuration for Expert game state.
	 @param master Configuration for Master game state.
	 */
	public static int getIntegerDependingOnGameState( IntValue normal, IntValue expert, IntValue master ) {
		IntValue value = getValueDependingOnGameState( normal, expert, master );

		return Config.getInteger( value );
	}

	/**
	 Returns double depending on current game state.

	 @param normal Configuration for Normal game state.
	 @param expert Configuration for Expert game state.
	 @param master Configuration for Master game state.
	 */
	public static double getDoubleDependingOnGameState( DoubleValue normal, DoubleValue expert, DoubleValue master ) {
		DoubleValue value = getValueDependingOnGameState( normal, expert, master );

		return Config.getDouble( value );
	}

	/**
	 Returns duration in seconds depending on current game state.

	 @param normal Configuration for Normal game state.
	 @param expert Configuration for Expert game state.
	 @param master Configuration for Master game state.
	 */
	public static int getDurationDependingOnGameState( DoubleValue normal, DoubleValue expert, DoubleValue master ) {
		DoubleValue value = getValueDependingOnGameState( normal, expert, master );

		return Config.getDurationInSeconds( value );
	}

	/**
	 Returns chance depending on current game state.

	 @param normal Configuration for Normal game state.
	 @param expert Configuration for Expert game state.
	 @param master Configuration for Master game state.
	 */
	public static double getChanceDependingOnGameState( DoubleValue normal, DoubleValue expert, DoubleValue master ) {
		DoubleValue value = getValueDependingOnGameState( normal, expert, master );

		return Config.getChance( value );
	}

	/**
	 Returns configuration value depending on current game state.

	 @param normal Configuration value for Normal game state.
	 @param expert Configuration value for Expert game state.
	 @param master Configuration value for Master game state.
	 */
	public static < ConfigType > ConfigType getValueDependingOnGameState( ConfigType normal, ConfigType expert, ConfigType master ) {
		switch( current ) {
			default:
				return normal;
			case EXPERT:
				return expert;
			case MASTER:
				return master;
		}
	}

	public static IFormattableTextComponent getNormalModeText() {
		return generateModeText( "normal", NORMAL_MODE_COLOR );
	}

	public static IFormattableTextComponent getExpertModeText() {
		return generateModeText( "expert", EXPERT_MODE_COLOR );
	}

	public static IFormattableTextComponent getMasterModeText() {
		return generateModeText( "master", MASTER_MODE_COLOR );
	}

	private static IFormattableTextComponent generateModeText( String modeName, TextFormatting color ) {
		IFormattableTextComponent text = new TranslationTextComponent( "majruszs_difficulty.states." + modeName );
		text.mergeStyle( color );
		text.mergeStyle( TextFormatting.BOLD );

		return text;
	}

	/** All possible game states. */
	public enum State {
		NORMAL, EXPERT, MASTER
	}
}
