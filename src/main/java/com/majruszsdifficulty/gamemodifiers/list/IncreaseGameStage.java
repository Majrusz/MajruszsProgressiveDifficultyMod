package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.configs.StageProgressConfig;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.BooleanConfig;
import com.mlib.config.EnumConfig;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnDeath;
import com.mlib.gamemodifiers.contexts.OnDimensionChanged;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;

@AutoInstance
public class IncreaseGameStage extends GameModifier {
	static final EnumConfig< GameStage.Stage > DEFAULT_GAME_STAGE = new EnumConfig<>( GameStage.Stage.NORMAL );
	final StageProgressConfig expertMode = new StageProgressConfig( "none", "minecraft:the_nether" );
	final StageProgressConfig masterMode = new StageProgressConfig( "minecraft:ender_dragon", "none" );
	final BooleanConfig enteringAnyDimensionStartsExpertMode = new BooleanConfig( true );

	public static GameStage.Stage getDefaultGameStage() {
		return DEFAULT_GAME_STAGE.get();
	}

	public IncreaseGameStage() {
		super( Registries.Modifiers.GAME_STAGE );

		new OnDimensionChanged.Context( this::startExpertMode )
			.addCondition( data->GameStage.getCurrentStage() == GameStage.Stage.NORMAL )
			.addCondition( data->this.expertMode.dimensionTriggersChange( data.to.location() ) || this.enteringAnyDimensionStartsExpertMode.isEnabled() )
			.insertTo( this );

		new OnDimensionChanged.Context( this::startMasterMode )
			.addCondition( data->GameStage.getCurrentStage() == GameStage.Stage.EXPERT )
			.addCondition( data->this.masterMode.dimensionTriggersChange( data.to.location() ) )
			.insertTo( this );

		new OnDeath.Context( this::startExpertMode )
			.addCondition( data->GameStage.getCurrentStage() == GameStage.Stage.NORMAL )
			.addCondition( data->this.expertMode.entityTriggersChange( EntityType.getKey( data.target.getType() ) ) )
			.insertTo( this );

		new OnDeath.Context( this::startMasterMode )
			.addCondition( data->GameStage.getCurrentStage() == GameStage.Stage.EXPERT )
			.addCondition( data->this.masterMode.entityTriggersChange( EntityType.getKey( data.target.getType() ) ) )
			.insertTo( this );

		this.addConfig( DEFAULT_GAME_STAGE.name( "default_mode" ).comment( "Game stage set at the beginning of a new world." ) )
			.addConfig( this.enteringAnyDimensionStartsExpertMode
				.name( "any_dimension_expert" )
				.comment( "Determines whether any dimension should start Expert Mode (useful for integration with other mods)." )
			).addConfig( this.expertMode.name( "ExpertMode" ).comment( "Determines what starts the Expert Mode." ) )
			.addConfig( this.masterMode.name( "MasterMode" ).comment( "Determines what starts the Master Mode." ) );
	}

	private void startExpertMode( OnDimensionChanged.Data data ) {
		this.startExpertMode( data.entity.getServer() );
	}

	private void startExpertMode( OnDeath.Data data ) {
		this.startExpertMode( data.target.getServer() );
	}

	private void startExpertMode( @Nullable MinecraftServer minecraftServer ) {
		if( minecraftServer == null )
			return;

		GameStage.changeModeWithAdvancement( GameStage.Stage.EXPERT, minecraftServer );
		sendMessageToAllPlayers( minecraftServer.getPlayerList(), "majruszsdifficulty.on_expert_mode_start", GameStage.EXPERT_MODE_COLOR );
	}

	private void startMasterMode( OnDimensionChanged.Data data ) {
		this.startMasterMode( data.entity.getServer() );
	}

	private void startMasterMode( OnDeath.Data data ) {
		this.startMasterMode( data.target.getServer() );
	}

	private void startMasterMode( @Nullable MinecraftServer minecraftServer ) {
		if( minecraftServer == null )
			return;

		GameStage.changeModeWithAdvancement( GameStage.Stage.MASTER, minecraftServer );
		sendMessageToAllPlayers( minecraftServer.getPlayerList(), "majruszsdifficulty.on_master_mode_start", GameStage.MASTER_MODE_COLOR );
	}

	private static void sendMessageToAllPlayers( PlayerList playerList, String translationKey, ChatFormatting textColor ) {
		MutableComponent message = new TranslatableComponent( translationKey ).withStyle( textColor, ChatFormatting.BOLD );
		playerList.getPlayers().forEach( player->player.displayClientMessage( message, false ) );
	}
}
