package com.majruszsdifficulty.features;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.events.OnEntitySpawned;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.events.base.CustomCondition;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;

public class JockeySpawn {
	private static final GameStageValue< Boolean > IS_ENABLED = GameStageValue.disabledOn( GameStage.NORMAL_ID );
	private static float CHANCE = 0.125f;
	private static boolean IS_SCALED_BY_CRD = false;

	static {
		OnEntitySpawned.listen( JockeySpawn::spawnSkeleton )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.chanceCRD( ()->CHANCE, ()->IS_SCALED_BY_CRD ) )
			.addCondition( CustomCondition.isEnabled( IS_ENABLED ) )
			.addCondition( data->!data.isLoadedFromDisk )
			.addCondition( data->data.entity.getType() == EntityType.SPIDER );

		Serializables.getStatic( Config.Features.class )
			.define( "jockey_spawn", JockeySpawn.class );

		Serializables.getStatic( JockeySpawn.class )
			.define( "is_enabled", Reader.map( Reader.bool() ), ()->IS_ENABLED.get(), v->IS_ENABLED.set( v ) )
			.define( "chance", Reader.number(), ()->CHANCE, v->CHANCE = Range.CHANCE.clamp( v ) )
			.define( "is_scaled_by_crd", Reader.bool(), ()->IS_SCALED_BY_CRD, v->IS_SCALED_BY_CRD = v );
	}

	private static void spawnSkeleton( OnEntitySpawned data ) {
		Skeleton skeleton = EntityHelper.createSpawner( ()->EntityType.SKELETON, data.getServerLevel() ).position( data.entity.position() ).spawn();
		if( skeleton != null ) {
			skeleton.startRiding( data.entity );
		}
	}
}
