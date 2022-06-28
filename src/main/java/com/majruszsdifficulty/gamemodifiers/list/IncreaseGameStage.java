package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.config.GameStageEnumConfig;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.configs.StageProgressConfig;
import com.mlib.config.BooleanConfig;
import com.mlib.gamemodifiers.contexts.OnDeathContext;
import com.mlib.gamemodifiers.contexts.OnDimensionChangedContext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class IncreaseGameStage extends GameModifier {
	static final StageProgressConfig EXPERT_MODE = new StageProgressConfig( "ExpertMode", "Determines what starts the Expert Mode.", "none", "minecraft:the_nether" );
	static final StageProgressConfig MASTER_MODE = new StageProgressConfig( "MasterMode", "Determines what starts the Master Mode.", "minecraft:ender_dragon", "none" );
	static final OnDimensionChangedContext ON_DIMENSION_CHANGED = new OnDimensionChangedContext( IncreaseGameStage::handleDimensionChange );
	static final OnDeathContext ON_DEATH = new OnDeathContext( IncreaseGameStage::handleKilledEntity );
	static final BooleanConfig ENTERING_ANY_DIMENSION_STARTS_EXPERT_MODE = new BooleanConfig( "any_dimension_expert", "Determines whether any dimension should start Expert Mode (useful for integration with other mods).", false, true );
	static final GameStageEnumConfig DEFAULT_GAME_STAGE = new GameStageEnumConfig( "default_mode", "Game stage set at the beginning of a new world.", false, GameStage.Stage.NORMAL );

	public static GameStage.Stage getDefaultGameStage() {
		return DEFAULT_GAME_STAGE.get();
	}

	public IncreaseGameStage() {
		super( GameModifier.GAME_STAGE, ON_DIMENSION_CHANGED, ON_DEATH );
		this.addConfigs( ENTERING_ANY_DIMENSION_STARTS_EXPERT_MODE, DEFAULT_GAME_STAGE, EXPERT_MODE, MASTER_MODE );
	}

	private static void handleDimensionChange( com.mlib.gamemodifiers.GameModifier gameModifier, OnDimensionChangedContext.Data data ) {
		GameStage.Stage currentGameStage = GameStage.getCurrentStage();
		@Nullable MinecraftServer server = data.entity.getServer();
		ResourceLocation dimension = data.to.location();
		if( currentGameStage == GameStage.Stage.NORMAL && ( EXPERT_MODE.dimensionTriggersChange( dimension ) || ENTERING_ANY_DIMENSION_STARTS_EXPERT_MODE.isEnabled() ) ) {
			startExpertMode( server );
		} else if( currentGameStage == GameStage.Stage.EXPERT && MASTER_MODE.dimensionTriggersChange( dimension ) ) {
			startMasterMode( server );
		}
	}

	private static void handleKilledEntity( com.mlib.gamemodifiers.GameModifier gameModifier, OnDeathContext.Data data ) {
		GameStage.Stage currentGameStage = GameStage.getCurrentStage();
		@Nullable MinecraftServer server = data.target.getServer();
		ResourceLocation entityType = EntityType.getKey( data.target.getType() );
		if( currentGameStage == GameStage.Stage.NORMAL && EXPERT_MODE.entityTriggersChange( entityType ) ) {
			startExpertMode( server );
		} else if( currentGameStage == GameStage.Stage.EXPERT && MASTER_MODE.entityTriggersChange( entityType ) ) {
			startMasterMode( server );
		}
	}

	private static void startExpertMode( @Nullable MinecraftServer minecraftServer ) {
		if( minecraftServer == null )
			return;

		GameStage.changeModeWithAdvancement( GameStage.Stage.EXPERT, minecraftServer );
		sendMessageToAllPlayers( minecraftServer.getPlayerList(), "majruszsdifficulty.on_expert_mode_start", GameStage.EXPERT_MODE_COLOR );
	}

	private static void startMasterMode( @Nullable MinecraftServer minecraftServer ) {
		if( minecraftServer == null )
			return;

		GameStage.changeModeWithAdvancement( GameStage.Stage.MASTER, minecraftServer );
		sendMessageToAllPlayers( minecraftServer.getPlayerList(), "majruszsdifficulty.on_master_mode_start", GameStage.MASTER_MODE_COLOR );
	}

	private static void sendMessageToAllPlayers( PlayerList playerList, String translationKey, ChatFormatting textColor ) {
		for( Player player : playerList.getPlayers() ) {
			MutableComponent message = Component.translatable( translationKey );
			message.withStyle( textColor, ChatFormatting.BOLD );

			player.displayClientMessage( message, false );
		}
	}
}
