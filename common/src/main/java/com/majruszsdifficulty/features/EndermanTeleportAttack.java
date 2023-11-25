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
import com.majruszsdifficulty.gamestage.GameStageValue;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.EnderMan;

public class EndermanTeleportAttack {
	private static final GameStageValue< Boolean > IS_ENABLED = GameStageValue.enabledOn( GameStage.MASTER_ID );
	private static float CHANCE = 0.5f;
	private static boolean IS_SCALED_BY_CRD = true;

	static {
		OnEntityDamaged.listen( EndermanTeleportAttack::teleportRandomly )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.chanceCRD( ()->CHANCE, ()->IS_SCALED_BY_CRD ) )
			.addCondition( CustomCondition.isEnabled( IS_ENABLED ) )
			.addCondition( data->data.attacker instanceof EnderMan )
			.addCondition( data->!data.source.isIndirect() );

		Serializables.getStatic( Config.Features.class )
			.define( "enderman_teleport_attack", EndermanTeleportAttack.class );

		Serializables.getStatic( EndermanTeleportAttack.class )
			.define( "is_enabled", Reader.map( Reader.bool() ), ()->IS_ENABLED.get(), v->IS_ENABLED.set( v ) )
			.define( "chance", Reader.number(), ()->CHANCE, v->CHANCE = Range.CHANCE.clamp( v ) )
			.define( "is_scaled_by_crd", Reader.bool(), ()->IS_SCALED_BY_CRD, v->IS_SCALED_BY_CRD = v );
	}

	private static void teleportRandomly( OnEntityDamaged data ) {
		if( LevelHelper.teleportNearby( data.target, data.getServerLevel(), 6.0 ) && data.target instanceof ServerPlayer player ) {
			MajruszsDifficulty.HELPER.triggerAchievement( player, "enderman_teleport_attack" );
		}
	}
}
