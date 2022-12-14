package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.mlib.gamemodifiers.GameModifier;import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.configs.StageProgressConfig;
import com.mlib.config.BooleanConfig;
import com.mlib.config.EnumConfig;
import com.mlib.gamemodifiers.contexts.OnDeath;
import com.mlib.gamemodifiers.contexts.OnDimensionChanged;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;

public class IncreaseGameStage extends GameModifier {
	static final EnumConfig< GameStage.Stage > DEFAULT_GAME_STAGE = new EnumConfig<>( "default_mode", "Game stage set at the beginning of a new world.", false, GameStage.Stage.NORMAL );
	final StageProgressConfig expertMode = new StageProgressConfig( "ExpertMode", "Determines what starts the Expert Mode.", "none", "minecraft:the_nether" );
	final StageProgressConfig masterMode = new StageProgressConfig( "MasterMode", "Determines what starts the Master Mode.", "minecraft:ender_dragon", "none" );
	final BooleanConfig enteringAnyDimensionStartsExpertMode = new BooleanConfig( "any_dimension_expert", "Determines whether any dimension should start Expert Mode (useful for integration with other mods).", false, true );

	public static GameStage.Stage getDefaultGameStage() {
		return DEFAULT_GAME_STAGE.get();
	}

	public IncreaseGameStage() {
		super( Registries.Modifiers.GAME_STAGE, "", "" );

		OnDimensionChanged.Context onExpertDimension = new OnDimensionChanged.Context( this::startExpertMode );
		onExpertDimension.addCondition( data->GameStage.getCurrentStage() == GameStage.Stage.NORMAL )
			.addCondition( data->this.expertMode.dimensionTriggersChange( data.to.location() ) || this.enteringAnyDimensionStartsExpertMode.isEnabled() );

		OnDimensionChanged.Context onMasterDimension = new OnDimensionChanged.Context( this::startMasterMode );
		onMasterDimension.addCondition( data->GameStage.getCurrentStage() == GameStage.Stage.EXPERT )
			.addCondition( data->this.masterMode.dimensionTriggersChange( data.to.location() ) );

		OnDeath.Context onExpertKill = new OnDeath.Context( this::startExpertMode );
		onExpertKill.addCondition( data->GameStage.getCurrentStage() == GameStage.Stage.NORMAL )
			.addCondition( data->this.expertMode.entityTriggersChange( EntityType.getKey( data.target.getType() ) ) );

		OnDeath.Context onMasterKill = new OnDeath.Context( this::startMasterMode );
		onMasterKill.addCondition( data->GameStage.getCurrentStage() == GameStage.Stage.EXPERT )
			.addCondition( data->this.masterMode.entityTriggersChange( EntityType.getKey( data.target.getType() ) ) );

		this.addContexts( onExpertDimension, onMasterDimension, onExpertKill, onMasterKill );
		this.addConfigs( DEFAULT_GAME_STAGE, this.enteringAnyDimensionStartsExpertMode, this.expertMode, this.masterMode );
	}

	private void startExpertMode( OnDimensionChanged.Data data ) {
		startExpertMode( data.entity.getServer() );
	}

	private void startExpertMode( OnDeath.Data data ) {
		startExpertMode( data.target.getServer() );
	}

	private void startExpertMode( @Nullable MinecraftServer minecraftServer ) {
		if( minecraftServer == null )
			return;

		GameStage.changeModeWithAdvancement( GameStage.Stage.EXPERT, minecraftServer );
		sendMessageToAllPlayers( minecraftServer.getPlayerList(), "majruszsdifficulty.on_expert_mode_start", GameStage.EXPERT_MODE_COLOR );
	}

	private void startMasterMode( OnDimensionChanged.Data data ) {
		startMasterMode( data.entity.getServer() );
	}

	private void startMasterMode( OnDeath.Data data ) {
		startMasterMode( data.target.getServer() );
	}

	private void startMasterMode( @Nullable MinecraftServer minecraftServer ) {
		if( minecraftServer == null )
			return;

		GameStage.changeModeWithAdvancement( GameStage.Stage.MASTER, minecraftServer );
		sendMessageToAllPlayers( minecraftServer.getPlayerList(), "majruszsdifficulty.on_master_mode_start", GameStage.MASTER_MODE_COLOR );
	}

	private static void sendMessageToAllPlayers( PlayerList playerList, String translationKey, ChatFormatting textColor ) {
		MutableComponent message = Component.translatable( translationKey ).withStyle( textColor, ChatFormatting.BOLD );
		playerList.getPlayers().forEach( player -> player.displayClientMessage( message, false ) );
	}
}
