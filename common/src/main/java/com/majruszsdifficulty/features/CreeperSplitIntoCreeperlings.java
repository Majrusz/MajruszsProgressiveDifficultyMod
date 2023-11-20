package com.majruszsdifficulty.features;

import com.majruszlibrary.collection.DefaultMap;
import com.majruszlibrary.contexts.OnEntityDied;
import com.majruszlibrary.contexts.OnExploded;
import com.majruszlibrary.contexts.base.Condition;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.math.AnyPos;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.entity.CreeperlingEntity;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.phys.AABB;

public class CreeperSplitIntoCreeperlings {
	private static final GameStageValue< Integer > COUNT = GameStageValue.of(
		DefaultMap.defaultEntry( 2 ),
		DefaultMap.entry( GameStage.EXPERT_ID, 4 ),
		DefaultMap.entry( GameStage.MASTER_ID, 6 )
	);
	private static float CHANCE = 0.666f;
	private static boolean IS_SCALED_BY_CRD = true;

	static {
		OnExploded.listen( CreeperSplitIntoCreeperlings::spawnCreeperlings )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.chanceCRD( ()->CHANCE, ()->IS_SCALED_BY_CRD ) )
			.addCondition( data->data.explosion.getDirectSourceEntity() != null )
			.addCondition( data->data.explosion.getDirectSourceEntity().getType().equals( EntityType.CREEPER ) );

		OnExploded.listen( CreeperSplitIntoCreeperlings::giveAdvancement )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->data.explosion.getDirectSourceEntity() instanceof CreeperlingEntity );

		OnEntityDied.listen( CreeperSplitIntoCreeperlings::giveAdvancement )
			.addCondition( data->data.attacker instanceof ServerPlayer )
			.addCondition( data->data.target instanceof CreeperlingEntity );

		Serializables.getStatic( Config.Features.class )
			.define( "creeper_split_into_creeperlings", CreeperSplitIntoCreeperlings.class );

		Serializables.getStatic( CreeperSplitIntoCreeperlings.class )
			.define( "count", Reader.map( Reader.integer() ), ()->COUNT.get(), v->COUNT.set( v ) )
			.define( "chance", Reader.number(), ()->CHANCE, v->CHANCE = Range.CHANCE.clamp( v ) )
			.define( "is_scaled_by_crd", Reader.bool(), ()->IS_SCALED_BY_CRD, v->IS_SCALED_BY_CRD = v );
	}

	private static void spawnCreeperlings( OnExploded data ) {
		Creeper creeper = ( Creeper )data.explosion.getDirectSourceEntity();
		GameStage gameStage = GameStageHelper.determineGameStage( data );
		int count = Random.nextInt( 1, COUNT.get( gameStage ) + 1 );
		for( int i = 0; i < count; ++i ) {
			CreeperlingEntity creeperling = EntityHelper.createSpawner( MajruszsDifficulty.CREEPERLING, data.getLevel() )
				.position( AnyPos.from( creeper.blockPosition() ).add( Random.nextVector( -2, 2, -1, 1, -2, 2 ) ).center().vec3() )
				.mobSpawnType( MobSpawnType.EVENT )
				.spawn();

			data.skipEntityIf( entity->entity.equals( creeperling ) );
		}
	}

	private static void giveAdvancement( OnExploded data ) {
		data.getServerLevel()
			.getEntitiesOfClass( ServerPlayer.class, new AABB( AnyPos.from( data.position ).block() ).inflate( 10.0, 6.0, 10.0 ) )
			.forEach( CreeperSplitIntoCreeperlings::giveAdvancement );
	}

	private static void giveAdvancement( OnEntityDied data ) {
		CreeperSplitIntoCreeperlings.giveAdvancement( ( ServerPlayer )data.attacker );
	}

	private static void giveAdvancement( ServerPlayer player ) {
		MajruszsDifficulty.HELPER.triggerAchievement( player, "encountered_creeperling" );
	}
}
