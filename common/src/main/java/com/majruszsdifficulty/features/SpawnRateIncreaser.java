package com.majruszsdifficulty.features;

import com.majruszlibrary.collection.DefaultMap;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.events.OnMobSpawnLimitGet;
import com.majruszlibrary.events.OnMobSpawnRateGet;
import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import net.minecraft.world.entity.MobCategory;

public class SpawnRateIncreaser {
	private static boolean IS_ENABLED = true;
	private static GameStageValue< Float > MULTIPLIER = GameStageValue.of(
		DefaultMap.defaultEntry( 1.0f ),
		DefaultMap.entry( GameStage.EXPERT_ID, 1.1f ),
		DefaultMap.entry( GameStage.MASTER_ID, 1.2f )
	);

	static {
		OnMobSpawnRateGet.listen( SpawnRateIncreaser::increase )
			.addCondition( data->IS_ENABLED )
			.addCondition( data->data.category.equals( MobCategory.MONSTER ) );

		OnMobSpawnLimitGet.listen( SpawnRateIncreaser::increase )
			.addCondition( data->IS_ENABLED )
			.addCondition( data->data.category.equals( MobCategory.MONSTER ) );

		Serializables.getStatic( Config.Features.class )
			.define( "spawn_rate_increaser", SpawnRateIncreaser.class );

		Serializables.getStatic( SpawnRateIncreaser.class )
			.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v )
			.define( "multiplier", Reader.map( Reader.number() ), ()->MULTIPLIER.get(), v->MULTIPLIER = GameStageValue.of( Range.of( 1.0f, 20.0f )
				.clamp( v ) )
			);
	}

	private static void increase( OnMobSpawnRateGet data ) {
		data.value += data.original * ( MULTIPLIER.get( GameStageHelper.getGlobalGameStage() ) - 1.0f );
	}

	private static void increase( OnMobSpawnLimitGet data ) {
		data.value += data.original * ( MULTIPLIER.get( GameStageHelper.getGlobalGameStage() ) - 1.0f );
	}

}
