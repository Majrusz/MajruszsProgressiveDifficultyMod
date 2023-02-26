package com.majruszsdifficulty;

import com.majruszsdifficulty.gamemodifiers.contexts.OnGameStageChange;
import com.mlib.levels.LevelHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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

			OnGameStageChange.accept( new OnGameStageChange.Data( server, previous, CURRENT ) );
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
		return Component.translatable( "majruszsdifficulty.stages." + stage.name().toLowerCase() ).withStyle( stage.formatting );
	}

	public static double getRegionalDifficulty( Entity target ) {
		double clampedRegionalDifficulty = target != null ? LevelHelper.getClampedRegionalDifficulty( target ) : 0.25;

		return Mth.clamp( clampedRegionalDifficulty + getStageModifier(), 0.0, 1.0 );
	}

	public static double getRegionalDifficulty( Level level, Vec3 position ) {
		double clampedRegionalDifficulty = LevelHelper.getClampedRegionalDifficulty( level, position );

		return Mth.clamp( clampedRegionalDifficulty + getStageModifier(), 0.0, 1.0 );
	}

	public static double getStageModifier() {
		return getCurrentGameStageDependentValue( 0.0, 0.15, 0.3 );
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
