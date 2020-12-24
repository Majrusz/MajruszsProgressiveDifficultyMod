package com.majruszs_difficulty;

import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class GameState {
	private static Mode current = Mode.NORMAL;

	public enum Mode {
		NORMAL, EXPERT, MASTER
	}

	public static boolean changeMode( Mode mode ) {
		if( mode == current )
			return false;

		current = mode;
		return true;
	}

	public static Mode getCurrentMode() {
		return current;
	}

	public static boolean atLeast( Mode mode ) {
		if( mode == Mode.EXPERT ) {
			return ( current == Mode.EXPERT || current == Mode.MASTER );
		} else if( mode == Mode.MASTER ) {
			return current == Mode.MASTER;
		} else
			return true;
	}

	public static int convertModeToInteger( Mode mode ) {
		switch( mode ) {
			default:
				return 0;
			case EXPERT:
				return 1;
			case MASTER:
				return 2;
		}
	}

	public static Mode convertIntegerToMode( int mode ) {
		switch( mode ) {
			default:
				return Mode.NORMAL;
			case 1:
				return Mode.EXPERT;
			case 2:
				return Mode.MASTER;
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
		text.func_240699_a_( color );
		text.func_240699_a_( TextFormatting.BOLD );

		return text;
	}
}
