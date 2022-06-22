package com.majruszsdifficulty;

import com.majruszsdifficulty.config.GameStageEnumConfig;
import com.mlib.config.BooleanConfig;
import com.mlib.config.StringListConfig;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

import static com.majruszsdifficulty.MajruszsDifficulty.STATE_GROUP;

public class GameStageConfig {
	protected final StringListConfig entitiesStartingExpertMode;
	protected final StringListConfig entitiesStartingMasterMode;
	protected final StringListConfig dimensionsStartingExpertMode;
	protected final StringListConfig dimensionsStartingMasterMode;
	protected final BooleanConfig enteringAnyDimensionStartsExpertMode;
	protected final GameStageEnumConfig defaultGameStage;

	public GameStageConfig() {
		String entitiesExpertComment = "List of entities which start Expert Mode after killing them. (at least one)";
		this.entitiesStartingExpertMode = new StringListConfig( "entities_expert", entitiesExpertComment, false, "none" );

		String entitiesMasterComment = "List of entities which start Master Mode after killing them.";
		this.entitiesStartingMasterMode = new StringListConfig( "entities_master", entitiesMasterComment, false, "minecraft:ender_dragon" );

		String dimensionsExpertComment = "List of dimensions that start Expert Mode when a player enters it for the first time. (ignored when any_dimension_expert is set to true)";
		this.dimensionsStartingExpertMode = new StringListConfig( "dimensions_expert", dimensionsExpertComment, false, "minecraft:the_nether" );

		String dimensionsMasterComment = "List of dimensions that start Master Mode when a player enters it for the first time.";
		this.dimensionsStartingMasterMode = new StringListConfig( "dimensions_master", dimensionsMasterComment, false, "none" );

		String anyDimensionComment = "Should entering any dimension start Expert Mode?";
		this.enteringAnyDimensionStartsExpertMode = new BooleanConfig( "any_dimension_expert", anyDimensionComment, false, true );

		String defaultComment = "game stage that is always set at the beginning of the game.";
		this.defaultGameStage = new GameStageEnumConfig( "default_game_state", defaultComment, false, GameStage.Stage.NORMAL );

		STATE_GROUP.addConfigs( this.entitiesStartingExpertMode, this.entitiesStartingMasterMode, this.dimensionsStartingExpertMode, this.dimensionsStartingMasterMode, this.enteringAnyDimensionStartsExpertMode, this.defaultGameStage );
	}

	/** Checks whether entering given dimension should start Expert Mode. */
	public boolean shouldDimensionStartExpertMode( ResourceLocation dimensionLocation ) {
		return this.enteringAnyDimensionStartsExpertMode.isEnabled() || this.dimensionsStartingExpertMode.contains( dimensionLocation.toString() );
	}

	/** Checks whether entering given dimension should start Master Mode. */
	public boolean shouldDimensionStartMasterMode( ResourceLocation dimensionLocation ) {
		return this.dimensionsStartingMasterMode.contains( dimensionLocation.toString() );
	}

	/** Checks whether killing given entity should start Expert Mode. */
	public boolean shouldKillingEntityStartExpertMode( @Nullable ResourceLocation entityLocation ) {
		return entityLocation != null && this.entitiesStartingExpertMode.contains( entityLocation.toString() );
	}

	/** Checks whether killing given entity should start Master Mode. */
	public boolean shouldKillingEntityStartMasterMode( @Nullable ResourceLocation entityLocation ) {
		return entityLocation != null && this.entitiesStartingMasterMode.contains( entityLocation.toString() );
	}

	/** Returns default game stage after world is loaded for the first time. */
	public GameStage.Stage getDefaultState() {
		return this.defaultGameStage.get();
	}
}
