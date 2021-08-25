package com.majruszs_difficulty;

import com.mlib.LevelHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

/** Class representing current game state. On this class depends lot of difficulty improvements. */
public class GameState {
	public static final ChatFormatting NORMAL_MODE_COLOR = ChatFormatting.WHITE;
	public static final ChatFormatting EXPERT_MODE_COLOR = ChatFormatting.RED;
	public static final ChatFormatting MASTER_MODE_COLOR = ChatFormatting.DARK_PURPLE;
	private static State CURRENT = State.NORMAL;

	/** Changing current game state globally. */
	public static boolean changeMode( State state ) {
		if( state == CURRENT )
			return false;

		CURRENT = state;
		return true;
	}

	/** Changing current game state globally and triggers the advancement if possible. */
	public static boolean changeModeWithAdvancement( State state, MinecraftServer minecraftServer ) {
		if( !changeMode( state ) )
			return false;

		triggerAdvancement( minecraftServer );
		return true;
	}

	/** Triggers game state advancement for all players if possible. */
	public static void triggerAdvancement( MinecraftServer minecraftServer ) {
		minecraftServer.getPlayerList()
			.getPlayers()
			.forEach( GameState::triggerAdvancement );
	}

	/** Triggers game state advancement for player if possible. */
	public static void triggerAdvancement( ServerPlayer player ) {
		Instances.GAME_STATE_TRIGGER.trigger( player, CURRENT );
	}

	/** Returning current server game state. */
	public static State getCurrentMode() {
		return CURRENT;
	}

	/** Checking if current state is equal or higher than given state. */
	public static boolean atLeast( State state ) {
		return getValueDependingOnGameState( state, true, CURRENT == State.EXPERT || CURRENT == State.MASTER, CURRENT == State.MASTER );
	}

	/** Converting game state to integer. */
	public static int convertStateToInteger( State state ) {
		return getValueDependingOnGameState( state, 0, 1, 2 );
	}

	/** Converting integer to game state. */
	public static State convertIntegerToState( int mode ) {
		return switch( mode ) {
			default -> State.NORMAL;
			case 1 -> State.EXPERT;
			case 2 -> State.MASTER;
		};
	}

	/**
	 Returns configuration value depending on given game state.

	 @param state  Game state to test.
	 @param normal Configuration value for Normal game state.
	 @param expert Configuration value for Expert game state.
	 @param master Configuration value for Master game state.
	 */
	public static < ConfigType > ConfigType getValueDependingOnGameState( State state, ConfigType normal, ConfigType expert, ConfigType master ) {
		return switch( state ) {
			default -> normal;
			case EXPERT -> expert;
			case MASTER -> master;
		};
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
	public static MutableComponent getGameStateText( State state ) {
		String modeName = getValueDependingOnGameState( state, "normal", "expert", "master" );
		ChatFormatting textColor = getValueDependingOnGameState( state, NORMAL_MODE_COLOR, EXPERT_MODE_COLOR, MASTER_MODE_COLOR );

		return generateModeText( modeName, textColor );
	}

	/** Returns clamped regional difficulty increased by certain value depending on current game state. */
	public static double getRegionalDifficulty( LivingEntity target ) {
		double clampedRegionalDifficulty = target != null ? LevelHelper.getClampedRegionalDifficulty( target ) : 0.25;
		double stateModifier = getStateModifier();

		return Mth.clamp( clampedRegionalDifficulty + stateModifier, 0.0, 1.0 );
	}

	/** Returns clamped regional difficulty modifier depending on current game state. */
	public static double getStateModifier() {
		return getValueDependingOnCurrentGameState( 0.0, 0.15, 0.3 );
	}

	/** Returns formatted game state text. */
	private static MutableComponent generateModeText( String modeName, ChatFormatting color ) {
		MutableComponent text = new TranslatableComponent( "majruszs_difficulty.states." + modeName );
		text.withStyle( color, ChatFormatting.BOLD );

		return text;
	}

	/** All possible game states. */
	public enum State {
		NORMAL, EXPERT, MASTER
	}
}
