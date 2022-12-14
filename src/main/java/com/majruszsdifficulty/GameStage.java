package com.majruszsdifficulty;

import com.mlib.levels.LevelHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GameStage {
	public static final ChatFormatting NORMAL_MODE_COLOR = ChatFormatting.WHITE;
	public static final ChatFormatting EXPERT_MODE_COLOR = ChatFormatting.RED;
	public static final ChatFormatting MASTER_MODE_COLOR = ChatFormatting.DARK_PURPLE;
	private static Stage CURRENT = Stage.NORMAL;

	/** Changes current game stage globally. */
	public static boolean changeMode( Stage stage ) {
		if( stage == CURRENT )
			return false;

		CURRENT = stage;
		return true;
	}

	/** Changes current game stage globally and triggers the advancement if possible. */
	public static boolean changeModeWithAdvancement( Stage stage, MinecraftServer minecraftServer ) {
		if( !changeMode( stage ) )
			return false;

		triggerAdvancementForAllPlayers( minecraftServer );
		return true;
	}

	public static void triggerAdvancementForAllPlayers( MinecraftServer minecraftServer ) {
		minecraftServer.getPlayerList().getPlayers().forEach( GameStage::triggerAdvancement );
	}

	public static void triggerAdvancement( ServerPlayer player ) {
		Registries.GAME_STATE_TRIGGER.trigger( player, CURRENT );
	}

	public static Stage getCurrentStage() {
		return CURRENT;
	}

	public static boolean atLeast( Stage stage ) {
		return getGameStageDependentValue( stage, true, CURRENT == Stage.EXPERT || CURRENT == Stage.MASTER, CURRENT == Stage.MASTER );
	}

	public static int convertStageToInteger( Stage stage ) {
		return getGameStageDependentValue( stage, 0, 1, 2 );
	}

	public static Stage convertIntegerToStage( int mode ) {
		return switch( mode ) {
			default -> Stage.NORMAL;
			case 1 -> Stage.EXPERT;
			case 2 -> Stage.MASTER;
		};
	}

	public static ChatFormatting getChatFormatting( Stage stage ) {
		return getGameStageDependentValue( stage, ChatFormatting.WHITE, ChatFormatting.RED, ChatFormatting.DARK_PURPLE );
	}

	public static < ConfigType > ConfigType getGameStageDependentValue( Stage stage, ConfigType normal, ConfigType expert, ConfigType master ) {
		return switch( stage ) {
			default -> normal;
			case EXPERT -> expert;
			case MASTER -> master;
		};
	}

	public static < ConfigType > ConfigType getCurrentGameStageDependentValue( ConfigType normal, ConfigType expert, ConfigType master ) {
		return getGameStageDependentValue( CURRENT, normal, expert, master );
	}

	public static MutableComponent getGameStageText( Stage stage ) {
		String gameStageId = getGameStageDependentValue( stage, "normal", "expert", "master" );

		return constructGameStageText( gameStageId, getChatFormatting( stage ) );
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

	public static MutableComponent withStyle( MutableComponent component ) {
		return switch( CURRENT ) {
			case MASTER -> component.withStyle( GameStage.MASTER_MODE_COLOR, ChatFormatting.BOLD );
			case EXPERT -> component.withStyle( GameStage.EXPERT_MODE_COLOR, ChatFormatting.BOLD );
			default -> component;
		};
	}

	private static MutableComponent constructGameStageText( String stage, ChatFormatting color ) {
		return Component.translatable( "majruszsdifficulty.stages." + stage ).withStyle( color, ChatFormatting.BOLD );
	}

	public enum Stage {
		NORMAL, EXPERT, MASTER
	}

	public record Integer( int normal, int expert, int master ) {}

	public record Double( double normal, double expert, double master ) {}
}
