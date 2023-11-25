package com.majruszsdifficulty.features;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.events.OnEntityDamaged;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.level.LevelHelper;
import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.events.base.CustomCondition;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.projectile.ThrownTrident;

public class DrownedLightningBolt {
	private static GameStage REQUIRED_GAME_STAGE = GameStageHelper.find( GameStage.EXPERT_ID );
	private static float CHANCE = 1.0f;
	private static boolean IS_SCALED_BY_CRD = false;

	static {
		OnEntityDamaged.listen( DrownedLightningBolt::spawn )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.chanceCRD( ()->CHANCE, ()->IS_SCALED_BY_CRD ) )
			.addCondition( CustomCondition.check( REQUIRED_GAME_STAGE ) )
			.addCondition( data->data.attacker instanceof Drowned )
			.addCondition( data->data.source.getDirectEntity() instanceof ThrownTrident )
			.addCondition( data->LevelHelper.isRainingAt( data.getLevel(), data.target.blockPosition().offset( 0, 2, 0 ) ) );

		Serializables.getStatic( Config.Features.class )
			.define( "drowned_lightning_bolt", DrownedLightningBolt.class );

		Serializables.getStatic( DrownedLightningBolt.class )
			.define( "required_game_stage", Reader.string(), ()->REQUIRED_GAME_STAGE.getId(), v->REQUIRED_GAME_STAGE = GameStageHelper.find( v ) )
			.define( "chance", Reader.number(), ()->CHANCE, v->CHANCE = Range.CHANCE.clamp( v ) )
			.define( "is_scaled_by_crd", Reader.bool(), ()->IS_SCALED_BY_CRD, v->IS_SCALED_BY_CRD = v );
	}

	private static void spawn( OnEntityDamaged data ) {
		EntityHelper.createSpawner( ()->EntityType.LIGHTNING_BOLT, data.getLevel() )
			.position( data.target.position() )
			.spawn();
	}
}
