package com.majruszs_difficulty.features;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.Instances;
import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.StringListConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

import static com.majruszs_difficulty.MajruszsDifficulty.STATE_GROUP;

/** Increasing game difficulty mode after certain meeting certain criteria. */
@Mod.EventBusSubscriber
public class IncreaseGameDifficulty {
	protected final ConfigGroup configGroup;
	protected final StringListConfig entitiesStartingExpertMode;
	protected final StringListConfig entitiesStartingMasterMode;
	protected final StringListConfig dimensionsStartingExpertMode;
	protected final StringListConfig dimensionsStartingMasterMode;
	protected final AvailabilityConfig enteringAnyDimensionStartsExpertMode;

	public IncreaseGameDifficulty() {
		this.configGroup = new ConfigGroup( "IncreasingDifficulty", "" );
		STATE_GROUP.addConfig( this.configGroup );

		String entitiesExpertComment = "List of entities starting Expert Mode after killing them.";
		String entitiesMasterComment = "List of entities starting Master Mode after killing them.";
		String dimensionsExpertComment = "List of dimensions starting Expert Mode when a player enters it for the first time. (ignored when any_dimension_expert is set to true)";
		String dimensionsMasterComment = "List of dimensions starting Master Mode when a player enters it for the first time.";
		String anyDimensionComment = "Should entering any dimension start Expert Mode?";
		this.entitiesStartingExpertMode = new StringListConfig( "entities_expert", entitiesExpertComment, false, "none" );
		this.entitiesStartingMasterMode = new StringListConfig( "entities_master", entitiesMasterComment, false, "minecraft:ender_dragon" );
		this.dimensionsStartingExpertMode = new StringListConfig( "dimensions_expert", dimensionsExpertComment, false, "minecraft:the_nether" );
		this.dimensionsStartingMasterMode = new StringListConfig( "dimensions_master", dimensionsMasterComment, false, "none" );
		this.enteringAnyDimensionStartsExpertMode = new AvailabilityConfig( "any_dimension_expert", anyDimensionComment, false, true );
		this.configGroup.addConfigs( this.entitiesStartingExpertMode, this.entitiesStartingMasterMode, this.dimensionsStartingExpertMode,
			this.dimensionsStartingMasterMode, this.enteringAnyDimensionStartsExpertMode
		);
	}

	@SubscribeEvent
	public static void onChangingDimension( PlayerEvent.PlayerChangedDimensionEvent event ) {
		IncreaseGameDifficulty gameDifficulty = Instances.INCREASE_GAME_DIFFICULTY;
		PlayerEntity player = event.getPlayer();

		switch( GameState.getCurrentMode() ) {
			case NORMAL:
				gameDifficulty.handleDimensionExpertMode( player, event.getTo() );
				break;
			case EXPERT:
				gameDifficulty.handleDimensionMasterMode( player, event.getTo() );
				break;
		}
	}

	@SubscribeEvent
	public static void onKillingEntity( LivingDeathEvent event ) {
		IncreaseGameDifficulty gameDifficulty = Instances.INCREASE_GAME_DIFFICULTY;
		LivingEntity entity = event.getEntityLiving();

		switch( GameState.getCurrentMode() ) {
			case NORMAL:
				gameDifficulty.handleKillingEntityExpertMode( entity );
				break;
			case EXPERT:
				gameDifficulty.handleKillingEntityMasterMode( entity );
				break;
		}
	}

	/** Changes current game state to Expert Mode if dimension conditions are met. */
	protected void handleDimensionExpertMode( PlayerEntity player, RegistryKey< World > dimension ) {
		if( !shouldDimensionStartExpertMode( dimension.getLocation() ) )
			return;

		startExpertMode( player.getServer() );
	}

	/** Checks whether entering given dimension should start Expert Mode. */
	protected boolean shouldDimensionStartExpertMode( ResourceLocation dimensionLocation ) {
		return this.enteringAnyDimensionStartsExpertMode.isEnabled() || this.dimensionsStartingExpertMode.contains( dimensionLocation.toString() );
	}

	/** Changes current game state to Master Mode if dimension conditions are met. */
	protected void handleDimensionMasterMode( PlayerEntity player, RegistryKey< World > dimension ) {
		if( !shouldDimensionStartMasterMode( dimension.getLocation() ) )
			return;

		startMasterMode( player.getServer() );
	}

	/** Checks whether entering given dimension should start Master Mode. */
	protected boolean shouldDimensionStartMasterMode( ResourceLocation dimensionLocation ) {
		return this.dimensionsStartingMasterMode.contains( dimensionLocation.toString() );
	}

	/** Changes current game state to Expert Mode if entity conditions are met. */
	protected void handleKillingEntityExpertMode( LivingEntity entity ) {
		EntityType< ? > entityType = entity.getType();
		if( !shouldKillingEntityStartExpertMode( entityType.getRegistryName() ) )
			return;

		startExpertMode( entity.getServer() );
	}

	/** Checks whether killing given entity should start Expert Mode. */
	protected boolean shouldKillingEntityStartExpertMode( @Nullable ResourceLocation entityLocation ) {
		return entityLocation != null && this.entitiesStartingExpertMode.contains( entityLocation.toString() );
	}

	/** Changes current game state to Expert Mode if entity conditions are met. */
	protected void handleKillingEntityMasterMode( LivingEntity entity ) {
		EntityType< ? > entityType = entity.getType();
		if( !shouldKillingEntityStartMasterMode( entityType.getRegistryName() ) )
			return;

		startMasterMode( entity.getServer() );
	}

	/** Checks whether killing given entity should start Master Mode. */
	protected boolean shouldKillingEntityStartMasterMode( @Nullable ResourceLocation entityLocation ) {
		return entityLocation != null && this.entitiesStartingMasterMode.contains( entityLocation.toString() );
	}

	/** Starts Expert Mode and sends message to all players. */
	protected void startExpertMode( @Nullable MinecraftServer minecraftServer ) {
		if( minecraftServer == null )
			return;

		GameState.changeModeWithAdvancement( GameState.State.EXPERT, minecraftServer );
		sendMessageToAllPlayers( minecraftServer.getPlayerList(), "majruszs_difficulty.on_expert_mode_start", GameState.EXPERT_MODE_COLOR );
	}

	/** Starts Master Mode and sends message to all players. */
	protected void startMasterMode( @Nullable MinecraftServer minecraftServer ) {
		if( minecraftServer == null )
			return;

		GameState.changeModeWithAdvancement( GameState.State.MASTER, minecraftServer );
		sendMessageToAllPlayers( minecraftServer.getPlayerList(), "majruszs_difficulty.on_master_mode_start", GameState.MASTER_MODE_COLOR );
	}

	/** Sends message to all players depending on current language. */
	protected void sendMessageToAllPlayers( PlayerList playerList, String translationKey, TextFormatting textColor ) {
		for( PlayerEntity player : playerList.getPlayers() ) {
			IFormattableTextComponent message = new TranslationTextComponent( translationKey );
			message.mergeStyle( textColor, TextFormatting.BOLD );

			player.sendStatusMessage( message, false );
		}
	}
}
