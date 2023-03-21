package com.majruszsdifficulty.gamemodifiers;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.Registries;
import com.mlib.config.StringListConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.Priority;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.majruszsdifficulty.gamemodifiers.configs.MobGroupConfig.LEADER_TAG;
import static com.majruszsdifficulty.gamemodifiers.configs.MobGroupConfig.SIDEKICK_TAG;

public class CustomConditions {
	public static < DataType > Condition< DataType > gameStage( GameStage... defaultGameStages ) {
		StringListConfig gameStages = new StringListConfig( Stream.of( defaultGameStages ).map( Enum::name ).toArray( String[]::new ) );

		return new Condition< DataType >( data->gameStages.contains( GameStage.getCurrentStage().name() ) )
			.priority( Priority.HIGH )
			.configurable( true )
			.addConfig( gameStages.name( "game_stages" ).comment( "Determines in which game stages it can happen." ) );
	}

	public static < DataType > Condition< DataType > gameStageAtLeast( GameStage defaultGameStage ) {
		return gameStage( Stream.of( GameStage.values() ).filter( gameStage->gameStage.ordinal() >= defaultGameStage.ordinal() ).toArray( GameStage[]::new ) );
	}

	public static < DataType > Condition< DataType > isNotPartOfGroup( Function< DataType, Entity > entity ) {
		Predicate< DataType > predicate = data->{
			return entity.apply( data ) instanceof PathfinderMob mob
				&& !mob.getPersistentData().getBoolean( SIDEKICK_TAG )
				&& !mob.getPersistentData().getBoolean( LEADER_TAG );
		};

		return new Condition< DataType >( predicate )
			.priority( Priority.HIGH );
	}

	public static < DataType > Condition< DataType > isNotPartOfUndeadArmy( Function< DataType, Entity > entity ) {
		return new Condition< DataType >( data->Registries.getUndeadArmyManager().isPartOfUndeadArmy( entity.apply( data ) ) );
	}

	public static < DataType > Condition< DataType > isNotNearUndeadArmy( Function< DataType, Entity > entity ) {
		return new Condition< DataType >( data->Registries.getUndeadArmyManager().findNearestUndeadArmy( entity.apply( data ).blockPosition() ) == null );
	}
}
