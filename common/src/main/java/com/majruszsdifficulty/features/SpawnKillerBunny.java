package com.majruszsdifficulty.features;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.events.OnEntitySpawned;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.events.base.CustomCondition;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.world.entity.animal.Rabbit;

public class SpawnKillerBunny {
	private static boolean IS_ENABLED = true;
	private static GameStage REQUIRED_GAME_STAGE = GameStageHelper.find( GameStage.EXPERT_ID );
	private static float CHANCE = 0.1f;
	private static boolean IS_SCALED_BY_CRD = true;

	static {
		OnEntitySpawned.listen( SpawnKillerBunny::transformToKiller )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.chanceCRD( ()->CHANCE, ()->IS_SCALED_BY_CRD ) )
			.addCondition( data->IS_ENABLED )
			.addCondition( CustomCondition.check( REQUIRED_GAME_STAGE ) )
			.addCondition( data->!data.isLoadedFromDisk )
			.addCondition( data->data.entity instanceof Rabbit rabbit && !rabbit.isBaby() );

		Serializables.getStatic( Config.Features.class )
			.define( "spawn_killer_bunny", SpawnKillerBunny.class );

		Serializables.getStatic( SpawnKillerBunny.class )
			.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v )
			.define( "required_game_stage", Reader.string(), ()->REQUIRED_GAME_STAGE.getId(), v->REQUIRED_GAME_STAGE = GameStageHelper.find( v ) )
			.define( "chance", Reader.number(), ()->CHANCE, v->CHANCE = Range.CHANCE.clamp( v ) )
			.define( "is_scaled_by_crd", Reader.bool(), ()->IS_SCALED_BY_CRD, v->IS_SCALED_BY_CRD = v );
	}

	private static void transformToKiller( OnEntitySpawned data ) {
		( ( Rabbit )data.entity ).setRabbitType( Rabbit.TYPE_EVIL );
	}
}
