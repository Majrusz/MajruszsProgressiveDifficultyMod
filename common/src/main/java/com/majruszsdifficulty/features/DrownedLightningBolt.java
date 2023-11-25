package com.majruszsdifficulty.features;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.events.OnEntityDamaged;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.level.LevelHelper;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.events.base.CustomCondition;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.projectile.ThrownTrident;

public class DrownedLightningBolt {
	private static final GameStageValue< Boolean > IS_ENABLED = GameStageValue.disabledOn( GameStage.NORMAL_ID );

	static {
		OnEntityDamaged.listen( DrownedLightningBolt::spawn )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( CustomCondition.isEnabled( IS_ENABLED ) )
			.addCondition( data->data.attacker instanceof Drowned )
			.addCondition( data->data.source.getDirectEntity() instanceof ThrownTrident )
			.addCondition( data->LevelHelper.isRainingAt( data.getLevel(), data.target.blockPosition().offset( 0, 2, 0 ) ) );

		Serializables.getStatic( Config.Features.class )
			.define( "drowned_lightning_bolt", DrownedLightningBolt.class );

		Serializables.getStatic( DrownedLightningBolt.class )
			.define( "is_enabled", Reader.map( Reader.bool() ), ()->IS_ENABLED.get(), v->IS_ENABLED.set( v ) );
	}

	private static void spawn( OnEntityDamaged data ) {
		EntityHelper.createSpawner( ()->EntityType.LIGHTNING_BOLT, data.getLevel() )
			.position( data.target.position() )
			.spawn();
	}
}
