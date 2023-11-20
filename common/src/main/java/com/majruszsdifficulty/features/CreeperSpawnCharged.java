package com.majruszsdifficulty.features;

import com.majruszlibrary.contexts.OnEntitySpawned;
import com.majruszlibrary.contexts.base.Condition;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperSpawnCharged {
	private static final GameStageValue< Boolean > IS_ENABLED = GameStageValue.alwaysEnabled();
	private static float CHANCE = 0.125f;
	private static boolean IS_SCALED_BY_CRD = true;

	static {
		OnEntitySpawned.listen( CreeperSpawnCharged::charge )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.chanceCRD( ()->CHANCE, ()->IS_SCALED_BY_CRD ) )
			.addCondition( data->!data.isLoadedFromDisk )
			.addCondition( data->IS_ENABLED.get( GameStageHelper.determineGameStage( data ) ) )
			.addCondition( data->data.entity instanceof Creeper );

		Serializables.getStatic( Config.Features.class )
			.define( "creeper_spawn_charged", CreeperSpawnCharged.class );

		Serializables.getStatic( CreeperSpawnCharged.class )
			.define( "is_enabled", Reader.map( Reader.bool() ), ()->IS_ENABLED.get(), v->IS_ENABLED.set( v ) )
			.define( "chance", Reader.number(), ()->CHANCE, v->CHANCE = Range.CHANCE.clamp( v ) )
			.define( "is_scaled_by_crd", Reader.bool(), ()->IS_SCALED_BY_CRD, v->IS_SCALED_BY_CRD = v );
	}

	private static void charge( OnEntitySpawned data ) {
		Creeper creeper = ( Creeper )data.entity;
		LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create( data.getServerLevel() );
		if( lightningBolt != null ) {
			creeper.thunderHit( data.getServerLevel(), lightningBolt );
			creeper.clearFire();
		}
	}
}
