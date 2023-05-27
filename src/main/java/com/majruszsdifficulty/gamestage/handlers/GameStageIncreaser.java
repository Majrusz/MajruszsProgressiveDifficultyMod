package com.majruszsdifficulty.gamestage.handlers;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.configs.StageProgressConfig;
import com.majruszsdifficulty.gamestage.GameStage;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.BooleanConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.EnumConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnDeath;
import com.mlib.gamemodifiers.contexts.OnDimensionChanged;
import net.minecraft.world.entity.EntityType;

@AutoInstance
public class GameStageIncreaser {
	static final EnumConfig< GameStage > DEFAULT_GAME_STAGE = new EnumConfig<>( GameStage.NORMAL );
	final StageProgressConfig expertMode = new StageProgressConfig( "none", "minecraft:the_nether" );
	final StageProgressConfig masterMode = new StageProgressConfig( "minecraft:ender_dragon", "none" );
	final BooleanConfig enteringAnyDimensionStartsExpertMode = new BooleanConfig( true );

	public static GameStage getDefaultGameStage() {
		return DEFAULT_GAME_STAGE.get();
	}

	public GameStageIncreaser() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.GAME_STAGE )
			.addConfig( DEFAULT_GAME_STAGE.name( "default_mode" ).comment( "Game stage set at the beginning of a new world." ) )
			.addConfig( this.enteringAnyDimensionStartsExpertMode
				.name( "any_dimension_expert" )
				.comment( "Determines whether any dimension should start Expert Mode (useful for integration with other mods)." )
			).addConfig( this.expertMode.name( "ExpertMode" ).comment( "Determines what starts the Expert Mode." ) )
			.addConfig( this.masterMode.name( "MasterMode" ).comment( "Determines what starts the Master Mode." ) );

		OnDimensionChanged.listen( this::startExpertMode )
			.addCondition( Condition.predicate( data->GameStage.getCurrentStage() == GameStage.NORMAL ) )
			.addCondition( Condition.predicate( data->this.expertMode.dimensionTriggersChange( data.to.location() ) || this.enteringAnyDimensionStartsExpertMode.isEnabled() ) )
			.insertTo( group );

		OnDimensionChanged.listen( this::startMasterMode )
			.addCondition( Condition.predicate( data->GameStage.getCurrentStage() == GameStage.EXPERT ) )
			.addCondition( Condition.predicate( data->this.masterMode.dimensionTriggersChange( data.to.location() ) ) )
			.insertTo( group );

		OnDeath.listen( this::startExpertMode )
			.addCondition( Condition.predicate( data->GameStage.getCurrentStage() == GameStage.NORMAL ) )
			.addCondition( Condition.predicate( data->this.expertMode.entityTriggersChange( EntityType.getKey( data.target.getType() ) ) ) )
			.insertTo( group );

		OnDeath.listen( this::startMasterMode )
			.addCondition( Condition.predicate( data->GameStage.getCurrentStage() == GameStage.EXPERT ) )
			.addCondition( Condition.predicate( data->this.masterMode.entityTriggersChange( EntityType.getKey( data.target.getType() ) ) ) )
			.insertTo( group );
	}

	private void startExpertMode( OnDimensionChanged.Data data ) {
		GameStage.changeStage( GameStage.EXPERT, data.entity.getServer() );
	}

	private void startExpertMode( OnDeath.Data data ) {
		GameStage.changeStage( GameStage.EXPERT, data.target.getServer() );
	}

	private void startMasterMode( OnDimensionChanged.Data data ) {
		GameStage.changeStage( GameStage.MASTER, data.entity.getServer() );
	}

	private void startMasterMode( OnDeath.Data data ) {
		GameStage.changeStage( GameStage.MASTER, data.target.getServer() );
	}
}
