package com.majruszsdifficulty.features;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.events.OnExploded;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.level.LevelHelper;
import com.majruszlibrary.math.AnyPos;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.events.base.CustomCondition;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;

public class DeadlierExplosions {
	private static boolean IS_ENABLED = true;
	private static GameStage REQUIRED_GAME_STAGE = GameStageHelper.find( GameStage.NORMAL_ID );
	private static float RADIUS_MULTIPLIER = 1.2599f;
	private static float FIRE_CHANCE = 0.75f;
	private static boolean IS_SCALED_BY_CRD = true;

	static {
		OnExploded.listen( DeadlierExplosions::giveTotem )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->IS_ENABLED )
			.addCondition( CustomCondition.check( REQUIRED_GAME_STAGE ) );

		Serializables.getStatic( Config.Features.class )
			.define( "deadlier_explosions", DeadlierExplosions.class );

		Serializables.getStatic( DeadlierExplosions.class )
			.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v )
			.define( "required_game_stage", Reader.string(), ()->REQUIRED_GAME_STAGE.getId(), v->REQUIRED_GAME_STAGE = GameStageHelper.find( v ) )
			.define( "radius_multiplier", Reader.number(), ()->RADIUS_MULTIPLIER, v->RADIUS_MULTIPLIER = Range.of( 1.0f, 10.0f ).clamp( v ) )
			.define( "fire_chance", Reader.number(), ()->FIRE_CHANCE, v->FIRE_CHANCE = Range.CHANCE.clamp( v ) )
			.define( "is_scaled_by_crd", Reader.bool(), ()->IS_SCALED_BY_CRD, v->IS_SCALED_BY_CRD = v );
	}

	private static void giveTotem( OnExploded data ) {
		float crd = IS_SCALED_BY_CRD ? ( float )LevelHelper.getClampedRegionalDifficultyAt( data.getLevel(), AnyPos.from( data.position ).block() ) : 1.0f;
		data.radius += data.originalRadius * crd * ( RADIUS_MULTIPLIER - 1.0f );
		if( Random.check( FIRE_CHANCE * crd ) ) {
			data.spawnsFire = true;
		}
	}
}
