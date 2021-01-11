package com.majruszs_difficulty;

import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

/** Class representing current game state. On this class depends lot of difficulty improvements. */
public class GameState {
	private static State current = State.NORMAL;

	/** All possible game states. */
	public enum State {
		NORMAL, EXPERT, MASTER
	}

	/** Changing current game state */
	public static boolean changeMode( State state ) {
		if( state == current )
			return false;

		current = state;
		return true;
	}

	public static State getCurrentMode() {
		return current;
	}

	public static boolean atLeast( State state ) {
		if( state == State.EXPERT ) {
			return ( current == State.EXPERT || current == State.MASTER );
		} else if( state == State.MASTER ) {
			return current == State.MASTER;
		} else
			return true;
	}

	public static int convertModeToInteger( State state ) {
		switch( state ) {
			default:
				return 0;
			case EXPERT:
				return 1;
			case MASTER:
				return 2;
		}
	}

	public static State convertIntegerToMode( int mode ) {
		switch( mode ) {
			default:
				return State.NORMAL;
			case 1:
				return State.EXPERT;
			case 2:
				return State.MASTER;
		}
	}

	public static final TextFormatting normalModeColor = TextFormatting.WHITE;
	public static IFormattableTextComponent getNormalModeText() {
		return generateModeText( "normal", normalModeColor );
	}

	public static final TextFormatting expertModeColor = TextFormatting.RED;
	public static IFormattableTextComponent getExpertModeText() {
		return generateModeText( "expert", expertModeColor );
	}

	public static final TextFormatting masterModeColor = TextFormatting.DARK_PURPLE;
	public static IFormattableTextComponent getMasterModeText() {
		return generateModeText( "master", masterModeColor );
	}

	private static IFormattableTextComponent generateModeText( String modeName, TextFormatting color ) {
		IFormattableTextComponent text = new TranslationTextComponent( "majruszs_difficulty.states." + modeName );
		text.mergeStyle( color );
		text.mergeStyle( TextFormatting.BOLD );

		return text;
	}
}
