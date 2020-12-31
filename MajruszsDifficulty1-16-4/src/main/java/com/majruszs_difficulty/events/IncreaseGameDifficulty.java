package com.majruszs_difficulty.events;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.raid.Raid;
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

		if( GameState.getCurrentMode() != GameState.Mode.NORMAL )
			return;

		if( !( playerEnteringDimension.getEntityWorld() instanceof ServerWorld ) )
			return;

		ServerWorld world = ( ServerWorld )playerEnteringDimension.getEntityWorld();

		GameState.changeMode( GameState.Mode.EXPERT );

		for( PlayerEntity player : world.getPlayers() ) {
			IFormattableTextComponent message = new TranslationTextComponent( "majruszs_difficulty.on_expert_mode_start" );
			message.mergeStyle( GameState.expertModeColor );
			message.mergeStyle( TextFormatting.BOLD );

			player.sendStatusMessage( message, false );
		}
	}

	@SubscribeEvent
	public static void enableMasterMode( LivingDeathEvent event ) {
		if( !( event.getEntityLiving() instanceof EnderDragonEntity ) )
			return;

		EnderDragonEntity dragon = ( EnderDragonEntity )event.getEntityLiving();

		if( !( dragon.getEntityWorld() instanceof ServerWorld ) )
			return;

		ServerWorld world = ( ServerWorld )dragon.getEntityWorld();

		if( GameState.getCurrentMode() == GameState.Mode.MASTER )
			return;

		GameState.changeMode( GameState.Mode.MASTER );

		for( PlayerEntity player : world.getPlayers() ) {
			IFormattableTextComponent message = new TranslationTextComponent( "majruszs_difficulty.on_master_mode_start" );
			message.mergeStyle( GameState.masterModeColor );
			message.mergeStyle( TextFormatting.BOLD );

			player.sendStatusMessage( message, false );
		}
	}
}
