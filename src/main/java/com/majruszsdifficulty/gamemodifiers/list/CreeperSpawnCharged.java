package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnSpawned;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Creeper;

@AutoInstance
public class CreeperSpawnCharged {
	public CreeperSpawnCharged() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT ).name( "CreeperSpawnCharged" ).comment( "Creeper may spawn charged." );

		OnSpawned.listenSafe( this::chargeCreeper )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.NORMAL ) )
			.addCondition( Condition.chanceCRD( 0.125, true ) )
			.addCondition( Condition.isServer() )
			.addCondition( Condition.excludable() )
			.addCondition( OnSpawned.isNotLoadedFromDisk() )
			.addCondition( Condition.predicate( data->data.target instanceof Creeper ) )
			.insertTo( group );
	}

	private void chargeCreeper( OnSpawned.Data data ) {
		Creeper creeper = ( Creeper )data.target;
		LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create( data.getServerLevel() );
		if( lightningBolt != null ) {
			creeper.thunderHit( data.getServerLevel(), lightningBolt );
			creeper.clearFire();
		}
	}
}
