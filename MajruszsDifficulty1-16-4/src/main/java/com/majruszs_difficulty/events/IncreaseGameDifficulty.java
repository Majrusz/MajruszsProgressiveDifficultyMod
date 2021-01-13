package com.majruszs_difficulty.events;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class IncreaseGameDifficulty {
	@SubscribeEvent
	public static void enableExpertMode( PlayerEvent.PlayerChangedDimensionEvent event ) {
		PlayerEntity playerEnteringDimension = event.getPlayer();

		if( !MajruszsHelper.isPlayerIn( playerEnteringDimension, DimensionType.THE_NETHER ) )
			return;

		if( GameState.getCurrentMode() != GameState.State.NORMAL )
			return;

		MinecraftServer minecraftServer = playerEnteringDimension.getServer();

		if( minecraftServer == null )
			return;

		GameState.changeMode( GameState.State.EXPERT );

		sendMessage( minecraftServer.getPlayerList(), "majruszs_difficulty.on_expert_mode_start", GameState.expertModeColor );
	}

	@SubscribeEvent
	public static void enableMasterMode( LivingDeathEvent event ) {
		if( !( event.getEntityLiving() instanceof EnderDragonEntity ) )
			return;

		EnderDragonEntity dragon = ( EnderDragonEntity )event.getEntityLiving();

		if( !( dragon.getEntityWorld() instanceof ServerWorld ) )
			return;

		if( GameState.getCurrentMode() == GameState.State.MASTER )
			return;

		MinecraftServer minecraftServer = dragon.getServer();

		if( minecraftServer == null )
			return;

		GameState.changeMode( GameState.State.MASTER );

		sendMessage( minecraftServer.getPlayerList(), "majruszs_difficulty.on_master_mode_start", GameState.masterModeColor );
	}

	protected static void sendMessage( PlayerList playerList, String translationKey, TextFormatting textColor ) {
		for( PlayerEntity player : playerList.getPlayers() ) {
			IFormattableTextComponent message = new TranslationTextComponent( translationKey );
			message.mergeStyle( textColor );
			message.mergeStyle( TextFormatting.BOLD );

			player.sendStatusMessage( message, false );
		}
	}
}
