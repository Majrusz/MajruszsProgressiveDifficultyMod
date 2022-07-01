package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.config.GameStageEnumConfig;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.configs.StageProgressConfig;
import com.mlib.config.BooleanConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDeathContext;
import com.mlib.gamemodifiers.contexts.OnDimensionChangedContext;
import com.mlib.gamemodifiers.data.OnDeathData;
import com.mlib.gamemodifiers.data.OnDimensionChangedData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class IncreaseGameStage extends GameModifier {
	static final GameStageEnumConfig DEFAULT_GAME_STAGE = new GameStageEnumConfig( "default_mode", "Game stage set at the beginning of a new world.", false, GameStage.Stage.NORMAL );
	final StageProgressConfig expertMode = new StageProgressConfig( "ExpertMode", "Determines what starts the Expert Mode.", "none", "minecraft:the_nether" );
	final StageProgressConfig masterMode = new StageProgressConfig( "MasterMode", "Determines what starts the Master Mode.", "minecraft:ender_dragon", "none" );
	final BooleanConfig enteringAnyDimensionStartsExpertMode = new BooleanConfig( "any_dimension_expert", "Determines whether any dimension should start Expert Mode (useful for integration with other mods).", false, true );

	public static GameStage.Stage getDefaultGameStage() {
		return DEFAULT_GAME_STAGE.get();
	}

	public IncreaseGameStage() {
		super( GameModifier.GAME_STAGE );

		OnDimensionChangedContext onExpertDimension = new OnDimensionChangedContext( this::startExpertMode );
		onExpertDimension.addCondition( new Condition.ContextOnDimensionChanged( data->GameStage.getCurrentStage() == GameStage.Stage.NORMAL ) )
			.addCondition( new Condition.ContextOnDimensionChanged( data->this.expertMode.dimensionTriggersChange( data.to.location() ) || this.enteringAnyDimensionStartsExpertMode.isEnabled() ) );

		OnDimensionChangedContext onMasterDimension = new OnDimensionChangedContext( this::startMasterMode );
		onMasterDimension.addCondition( new Condition.ContextOnDimensionChanged( data->GameStage.getCurrentStage() == GameStage.Stage.EXPERT ) )
			.addCondition( new Condition.ContextOnDimensionChanged( data->this.masterMode.dimensionTriggersChange( data.to.location() ) ) );

		OnDeathContext onExpertKill = new OnDeathContext( this::startExpertMode );
		onExpertKill.addCondition( new Condition.ContextOnDeath( data->GameStage.getCurrentStage() == GameStage.Stage.NORMAL ) )
			.addCondition( new Condition.ContextOnDeath( data->this.expertMode.entityTriggersChange( EntityType.getKey( data.target.getType() ) ) ) );

		OnDeathContext onMasterKill = new OnDeathContext( this::startMasterMode );
		onMasterKill.addCondition( new Condition.ContextOnDeath( data->GameStage.getCurrentStage() == GameStage.Stage.EXPERT ) )
			.addCondition( new Condition.ContextOnDeath( data->this.masterMode.entityTriggersChange( EntityType.getKey( data.target.getType() ) ) ) );

		this.addContexts( onExpertDimension, onMasterDimension, onExpertKill, onMasterKill );
		this.addConfigs( DEFAULT_GAME_STAGE, this.enteringAnyDimensionStartsExpertMode, this.expertMode, this.masterMode );
	}

	private void startExpertMode( OnDimensionChangedData data ) {
		startExpertMode( data.entity.getServer() );
	}

	private void startExpertMode( OnDeathData data ) {
		startExpertMode( data.target.getServer() );
	}

	private void startExpertMode( @Nullable MinecraftServer minecraftServer ) {
		if( minecraftServer == null )
			return;

		GameStage.changeModeWithAdvancement( GameStage.Stage.EXPERT, minecraftServer );
		sendMessageToAllPlayers( minecraftServer.getPlayerList(), "majruszsdifficulty.on_expert_mode_start", GameStage.EXPERT_MODE_COLOR );
	}

	private void startMasterMode( OnDimensionChangedData data ) {
		startExpertMode( data.entity.getServer() );
	}

	private void startMasterMode( OnDeathData data ) {
		startExpertMode( data.target.getServer() );
	}

	private void startMasterMode( @Nullable MinecraftServer minecraftServer ) {
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
