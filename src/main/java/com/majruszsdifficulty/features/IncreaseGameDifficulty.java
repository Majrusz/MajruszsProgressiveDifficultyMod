package com.majruszsdifficulty.features;

import com.majruszsdifficulty.GameState;
import com.majruszsdifficulty.Instances;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

/** Increasing game difficulty mode after certain meeting certain criteria. */
@Mod.EventBusSubscriber
public class IncreaseGameDifficulty {
	@SubscribeEvent
	public static void onChangingDimension( PlayerEvent.PlayerChangedDimensionEvent event ) {
		IncreaseGameDifficulty gameDifficulty = Instances.INCREASE_GAME_DIFFICULTY;
		Player player = event.getPlayer();

		switch( GameState.getCurrentMode() ) {
			case NORMAL -> gameDifficulty.handleDimensionExpertMode( player, event.getTo() );
			case EXPERT -> gameDifficulty.handleDimensionMasterMode( player, event.getTo() );
		}
	}

	@SubscribeEvent
	public static void onKillingEntity( LivingDeathEvent event ) {
		IncreaseGameDifficulty gameDifficulty = Instances.INCREASE_GAME_DIFFICULTY;
		LivingEntity entity = event.getEntityLiving();

		switch( GameState.getCurrentMode() ) {
			case NORMAL -> gameDifficulty.handleKillingEntityExpertMode( entity );
			case EXPERT -> gameDifficulty.handleKillingEntityMasterMode( entity );
		}
	}

	/** Changes current game state to Expert Mode if dimension conditions are met. */
	protected void handleDimensionExpertMode( Player player, ResourceKey< Level > dimension ) {
		if( !Instances.GAME_STATE_CONFIG.shouldDimensionStartExpertMode( dimension.location() ) )
			return;

		startExpertMode( player.getServer() );
	}

	/** Changes current game state to Master Mode if dimension conditions are met. */
	protected void handleDimensionMasterMode( Player player, ResourceKey< Level > dimension ) {
		if( !Instances.GAME_STATE_CONFIG.shouldDimensionStartMasterMode( dimension.location() ) )
			return;

		startMasterMode( player.getServer() );
	}

	/** Changes current game state to Expert Mode if entity conditions are met. */
	protected void handleKillingEntityExpertMode( LivingEntity entity ) {
		EntityType< ? > entityType = entity.getType();
		if( !Instances.GAME_STATE_CONFIG.shouldKillingEntityStartExpertMode( EntityType.getKey( entityType ) ) )
			return;

		startExpertMode( entity.getServer() );
	}

	/** Changes current game state to Expert Mode if entity conditions are met. */
	protected void handleKillingEntityMasterMode( LivingEntity entity ) {
		EntityType< ? > entityType = entity.getType();
		if( !Instances.GAME_STATE_CONFIG.shouldKillingEntityStartMasterMode( EntityType.getKey( entityType ) ) )
			return;

		startMasterMode( entity.getServer() );
	}

	/** Starts Expert Mode and sends message to all players. */
	protected void startExpertMode( @Nullable MinecraftServer minecraftServer ) {
		if( minecraftServer == null )
			return;

		GameState.changeModeWithAdvancement( GameState.State.EXPERT, minecraftServer );
		sendMessageToAllPlayers( minecraftServer.getPlayerList(), "majruszsdifficulty.on_expert_mode_start", GameState.EXPERT_MODE_COLOR );
	}

	/** Starts Master Mode and sends message to all players. */
	protected void startMasterMode( @Nullable MinecraftServer minecraftServer ) {
		if( minecraftServer == null )
			return;

		GameState.changeModeWithAdvancement( GameState.State.MASTER, minecraftServer );
		sendMessageToAllPlayers( minecraftServer.getPlayerList(), "majruszsdifficulty.on_master_mode_start", GameState.MASTER_MODE_COLOR );
	}

	/** Sends message to all players depending on current language. */
	protected void sendMessageToAllPlayers( PlayerList playerList, String translationKey, ChatFormatting textColor ) {
		for( Player player : playerList.getPlayers() ) {
			MutableComponent message = Component.translatable( translationKey );
			message.withStyle( textColor, ChatFormatting.BOLD );

			player.displayClientMessage( message, false );
		}
	}
}
