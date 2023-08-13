package com.majruszsdifficulty.gamestage;

import com.majruszsdifficulty.contexts.OnGameStageChange;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public enum GameStage {
	NORMAL( ChatFormatting.WHITE ),
	EXPERT( ChatFormatting.RED, ChatFormatting.BOLD ),
	MASTER( ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD );

	private static GameStage CURRENT = GameStage.NORMAL;
	final ChatFormatting[] formatting;

	GameStage( ChatFormatting... formatting ) {
		this.formatting = formatting;
	}

	public ChatFormatting[] getChatFormatting() {
		return this.formatting;
	}

	public static boolean changeStage( GameStage stage, @Nullable MinecraftServer server ) {
		if( stage != CURRENT ) {
			GameStage previous = CURRENT;
			CURRENT = stage;

			OnGameStageChange.dispatch( server, previous, CURRENT );
			return true;
		}

		return false;
	}

	public static GameStage getCurrentStage() {
		return CURRENT;
	}

	public static boolean atLeast( GameStage stage ) {
		return CURRENT.ordinal() >= stage.ordinal();
	}

	public static GameStage convertIntegerToStage( int mode ) {
		return switch( mode ) {
			default -> NORMAL;
			case 1 -> EXPERT;
			case 2 -> MASTER;
		};
	}

	public static < Type > Type getGameStageDependentValue( GameStage stage, Type normal, Type expert, Type master ) {
		return switch( stage ) {
			default -> normal;
			case EXPERT -> expert;
			case MASTER -> master;
		};
	}

	public static < Type > Type getCurrentGameStageDependentValue( Type normal, Type expert, Type master ) {
		return getGameStageDependentValue( CURRENT, normal, expert, master );
	}

	public static MutableComponent getGameStageText( GameStage stage ) {
		return new TranslatableComponent( "majruszsdifficulty.stages." + stage.name().toLowerCase() ).withStyle( stage.formatting );
	}

	public record Integer( int normal, int expert, int master ) {
		public Integer( int normal, int expert, int master ) {
			this.normal = normal;
			this.expert = expert;
			this.master = master;
		}

		public Integer( int value ) {
			this( value, value, value );
		}
	}

	public record Double( double normal, double expert, double master ) {
		public Double( double normal, double expert, double master ) {
			this.normal = normal;
			this.expert = expert;
			this.master = master;
		}

		public Double( double value ) {
			this( value, value, value );
		}
	}
}
