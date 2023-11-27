package com.majruszsdifficulty.features;

import com.majruszlibrary.collection.CollectionHelper;
import com.majruszlibrary.collection.DefaultMap;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.events.OnEntitySpawned;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.registry.Registries;
import com.majruszlibrary.text.RegexString;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import net.minecraft.world.entity.MobSpawnType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpawnBlocker {
	private static GameStageValue< List< RegexString > > FORBIDDEN_ENTITIES = GameStageValue.of(
		DefaultMap.defaultEntry( RegexString.toRegex( List.of( "majruszsdifficulty:illusioner", "majruszsdifficulty:tank", "majruszsdifficulty:cerberus" ) ) ),
		DefaultMap.entry( GameStage.EXPERT_ID, RegexString.toRegex( List.of( "majruszsdifficulty:cerberus" ) ) ),
		DefaultMap.entry( GameStage.MASTER_ID, List.of() )
	);

	static {
		OnEntitySpawned.listen( OnEntitySpawned::cancelSpawn )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->!data.isLoadedFromDisk )
			.addCondition( data->data.mobSpawnType == null || data.mobSpawnType.equals( MobSpawnType.NATURAL ) )
			.addCondition( SpawnBlocker.isForbidden() );

		Serializables.getStatic( Config.Features.class )
			.define( "spawn_blocker", SpawnBlocker.class );

		Serializables.getStatic( SpawnBlocker.class )
			.define( "forbidden_entities", Reader.map( Reader.list( Reader.string() ) ), SpawnBlocker::getEntities, SpawnBlocker::setEntities );
	}

	private static Condition< OnEntitySpawned > isForbidden() {
		return Condition.predicate( data->{
			String id = Registries.ENTITY_TYPES.getId( data.entity.getType() ).toString();
			List< RegexString > forbiddenIds = FORBIDDEN_ENTITIES.get( GameStageHelper.determineGameStage( data ) );
			for( RegexString forbiddenId : forbiddenIds ) {
				if( forbiddenId.matches( id ) ) {
					return true;
				}
			}

			return false;
		} );
	}

	private static Map< String, List< String > > getEntities() {
		return CollectionHelper.map( FORBIDDEN_ENTITIES.get(), RegexString::toString, HashMap::new );
	}

	private static void setEntities( Map< String, List< String > > map ) {
		FORBIDDEN_ENTITIES = GameStageValue.of( CollectionHelper.map( map, RegexString::toRegex, HashMap::new ) );
	}
}
