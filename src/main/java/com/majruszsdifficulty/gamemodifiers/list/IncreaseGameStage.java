package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.config.GameStageEnumConfig;
import com.majruszsdifficulty.gamemodifiers.CustomConfigs;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
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
	static final CustomConfigs.StageProgress EXPERT_MODE = new CustomConfigs.StageProgress( "ExpertMode", "Determines what starts the Expert Mode.", "none", "minecraft:the_nether" );
	static final CustomConfigs.StageProgress MASTER_MODE = new CustomConfigs.StageProgress( "MasterMode", "Determines what starts the Master Mode.", "minecraft:ender_dragon", "none" );
	static final OnDimensionChangedContext ON_DIMENSION_CHANGED = new OnDimensionChangedContext();
	static final OnDeathContext ON_DEATH = new OnDeathContext();
	static final BooleanConfig ENTERING_ANY_DIMENSION_STARTS_EXPERT_MODE = new BooleanConfig( "any_dimension_expert", "Determines whether any dimension should start Expert Mode (useful for integration with other mods).", false, true );
	static final GameStageEnumConfig DEFAULT_GAME_STAGE = new GameStageEnumConfig( "default_mode", "Game stage set at the beginning of a new world.", false, GameStage.Stage.NORMAL );

	public static GameStage.Stage getDefaultGameStage() {
		return DEFAULT_GAME_STAGE.get();
	}

	public IncreaseGameStage() {
		super( GameModifier.GAME_STAGE, "IncreaseGameStage", "Handles what may increase the game stage.", ON_DIMENSION_CHANGED, ON_DEATH );
		addConfigs( EXPERT_MODE, MASTER_MODE );
		this.configGroup.addConfigs( ENTERING_ANY_DIMENSION_STARTS_EXPERT_MODE, DEFAULT_GAME_STAGE );
	}

	@Override
	public void execute( Object data ) {
		GameStage.Stage currentGameStage = GameStage.getCurrentStage();
		boolean isNormal = currentGameStage == GameStage.Stage.NORMAL;
		boolean isExpert = currentGameStage == GameStage.Stage.EXPERT;
		if( data instanceof OnDimensionChangedContext.Data dimensionData ) {
			@Nullable MinecraftServer server = dimensionData.entity.getServer();
			ResourceLocation dimension = dimensionData.to.location();
			if( isNormal && ( EXPERT_MODE.dimensionTriggersChange( dimension ) || ENTERING_ANY_DIMENSION_STARTS_EXPERT_MODE.isEnabled() ) ) {
				startExpertMode( server );
			} else if( isExpert && MASTER_MODE.dimensionTriggersChange( dimension ) ) {
				startMasterMode( server );
			}
		} else if( data instanceof OnDeathContext.Data deathData ) {
			@Nullable MinecraftServer server = deathData.target.getServer();
			ResourceLocation entityType = EntityType.getKey( deathData.target.getType() );
			if( isNormal && EXPERT_MODE.entityTriggersChange( entityType ) ) {
				startExpertMode( server );
			} else if( isExpert && MASTER_MODE.entityTriggersChange( entityType ) ) {
				startMasterMode( server );
			}
		}
	}

	private void startExpertMode( @Nullable MinecraftServer minecraftServer ) {
		if( minecraftServer == null )
			return;

		GameStage.changeModeWithAdvancement( GameStage.Stage.EXPERT, minecraftServer );
		sendMessageToAllPlayers( minecraftServer.getPlayerList(), "majruszsdifficulty.on_expert_mode_start", GameStage.EXPERT_MODE_COLOR );
	}

	private void startMasterMode( @Nullable MinecraftServer minecraftServer ) {
		if( minecraftServer == null )
			return;

		GameStage.changeModeWithAdvancement( GameStage.Stage.MASTER, minecraftServer );
		sendMessageToAllPlayers( minecraftServer.getPlayerList(), "majruszsdifficulty.on_master_mode_start", GameStage.MASTER_MODE_COLOR );
	}

	private void sendMessageToAllPlayers( PlayerList playerList, String translationKey, ChatFormatting textColor ) {
		for( Player player : playerList.getPlayers() ) {
			MutableComponent message = Component.translatable( translationKey );
			message.withStyle( textColor, ChatFormatting.BOLD );

			player.displayClientMessage( message, false );
		}
	}
}
