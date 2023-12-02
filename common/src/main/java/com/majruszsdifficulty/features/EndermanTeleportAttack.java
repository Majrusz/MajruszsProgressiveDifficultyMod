package com.majruszsdifficulty.features;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.events.OnEntityDamaged;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.level.LevelHelper;
import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.events.base.CustomCondition;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.EnderMan;

public class EndermanTeleportAttack {
	private static boolean IS_ENABLED = true;
	private static GameStage REQUIRED_GAME_STAGE = GameStageHelper.find( GameStage.MASTER_ID );
	private static float CHANCE = 0.5f;
	private static boolean IS_SCALED_BY_CRD = true;

	static {
		OnEntityDamaged.listen( EndermanTeleportAttack::teleportRandomly )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.chanceCRD( ()->CHANCE, ()->IS_SCALED_BY_CRD ) )
			.addCondition( data->IS_ENABLED )
			.addCondition( CustomCondition.check( REQUIRED_GAME_STAGE ) )
			.addCondition( data->data.attacker instanceof EnderMan )
			.addCondition( data->!data.source.isIndirect() );

		Serializables.getStatic( Config.Features.class )
			.define( "enderman_teleport_attack", EndermanTeleportAttack.class );

		Serializables.getStatic( EndermanTeleportAttack.class )
			.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v )
			.define( "required_game_stage", Reader.string(), ()->REQUIRED_GAME_STAGE.getId(), v->REQUIRED_GAME_STAGE = GameStageHelper.find( v ) )
			.define( "chance", Reader.number(), ()->CHANCE, v->CHANCE = Range.CHANCE.clamp( v ) )
			.define( "is_scaled_by_crd", Reader.bool(), ()->IS_SCALED_BY_CRD, v->IS_SCALED_BY_CRD = v );
	}

	private static void teleportRandomly( OnEntityDamaged data ) {
		if( LevelHelper.teleportNearby( data.target, data.getServerLevel(), 6.0 ) && data.target instanceof ServerPlayer player ) {
			MajruszsDifficulty.HELPER.triggerAchievement( player, "enderman_teleport_attack" );
		}
	}
}
