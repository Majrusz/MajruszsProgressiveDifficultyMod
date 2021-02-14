package com.majruszs_difficulty.events;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.Instances;
import com.mlib.WorldHelper;
import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.majruszs_difficulty.MajruszsDifficulty.CONFIG_HANDLER;
import static com.majruszs_difficulty.MajruszsDifficulty.STATE_GROUP;

@Mod.EventBusSubscriber
public class IncreaseGameDifficulty {
	protected final ConfigGroup configGroup;
	protected final AvailabilityConfig dragonStartsMasterMode;
	protected final AvailabilityConfig witherStartsMasterMode;

	public IncreaseGameDifficulty() {
		this.configGroup = new ConfigGroup( "IncreasingDifficulty", "" );
		STATE_GROUP.addConfig( this.configGroup );

		String dragonComment = "Should killing Ender Dragon start Master Mode?";
		String witherComment = "Should killing Wither start Master Mode?";
		this.dragonStartsMasterMode = new AvailabilityConfig( "dragon_start_master_mode", dragonComment, false, true );
		this.witherStartsMasterMode = new AvailabilityConfig( "wither_start_master_mode", witherComment, false, false );
		this.configGroup.addConfigs( this.dragonStartsMasterMode, this.witherStartsMasterMode );
	}

	@SubscribeEvent
	public static void enableExpertMode( PlayerEvent.PlayerChangedDimensionEvent event ) {
		PlayerEntity playerEnteringDimension = event.getPlayer();

		if( !WorldHelper.isEntityIn( playerEnteringDimension, World.THE_NETHER ) )
			return;

		if( GameState.getCurrentMode() != GameState.State.NORMAL )
			return;

		MinecraftServer minecraftServer = playerEnteringDimension.getServer();

		if( minecraftServer == null )
			return;

		GameState.changeMode( GameState.State.EXPERT );

		sendMessage( minecraftServer.getPlayerList(), "majruszs_difficulty.on_expert_mode_start", GameState.EXPERT_MODE_COLOR );
	}

	@SubscribeEvent
	public static void enableMasterMode( LivingDeathEvent event ) {
		LivingEntity monster = event.getEntityLiving();

		if( GameState.getCurrentMode() == GameState.State.MASTER || !Instances.INCREASE_GAME_DIFFICULTY.isValidEntityToStartMasterMode( monster ) )
			return;

		MinecraftServer minecraftServer = monster.getServer();
		if( minecraftServer == null )
			return;

		GameState.changeMode( GameState.State.MASTER );
		sendMessage( minecraftServer.getPlayerList(), "majruszs_difficulty.on_master_mode_start", GameState.MASTER_MODE_COLOR );
	}

	private boolean isValidEntityToStartMasterMode( LivingEntity entity ) {
		boolean isDragon = entity instanceof EnderDragonEntity;
		boolean isWither = entity instanceof WitherEntity;
		boolean isServerWorld = entity.getEntityWorld() instanceof ServerWorld;

		return ( this.dragonStartsMasterMode.isEnabled() && isDragon || this.witherStartsMasterMode.isEnabled() && isWither ) && isServerWorld;
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
