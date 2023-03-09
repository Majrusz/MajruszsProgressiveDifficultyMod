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
import net.minecraft.world.entity.EntityType;

@AutoInstance
public class IncreaseGameStage extends GameModifier {
	static final EnumConfig< GameStage > DEFAULT_GAME_STAGE = new EnumConfig<>( GameStage.NORMAL );
	final StageProgressConfig expertMode = new StageProgressConfig( "none", "minecraft:the_nether" );
	final StageProgressConfig masterMode = new StageProgressConfig( "minecraft:ender_dragon", "none" );
	final BooleanConfig enteringAnyDimensionStartsExpertMode = new BooleanConfig( true );

	public static GameStage getDefaultGameStage() {
		return DEFAULT_GAME_STAGE.get();
	}

	public IncreaseGameStage() {
		super( Registries.Modifiers.GAME_STAGE );

		new OnDimensionChanged.Context( this::startExpertMode )
			.addCondition( data->GameStage.getCurrentStage() == GameStage.NORMAL )
			.addCondition( data->this.expertMode.dimensionTriggersChange( data.to.location() ) || this.enteringAnyDimensionStartsExpertMode.isEnabled() )
			.insertTo( this );

		new OnDimensionChanged.Context( this::startMasterMode )
			.addCondition( data->GameStage.getCurrentStage() == GameStage.EXPERT )
			.addCondition( data->this.masterMode.dimensionTriggersChange( data.to.location() ) )
			.insertTo( this );

		new OnDeath.Context( this::startExpertMode )
			.addCondition( data->GameStage.getCurrentStage() == GameStage.NORMAL )
			.addCondition( data->this.expertMode.entityTriggersChange( EntityType.getKey( data.target.getType() ) ) )
			.insertTo( this );

		new OnDeath.Context( this::startMasterMode )
			.addCondition( data->GameStage.getCurrentStage() == GameStage.EXPERT )
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
