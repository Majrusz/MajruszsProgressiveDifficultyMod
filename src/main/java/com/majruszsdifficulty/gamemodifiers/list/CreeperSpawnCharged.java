package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperSpawnCharged extends GameModifier {
	static final OnSpawnedContext ON_SPAWNED = new OnSpawnedContext( CreeperSpawnCharged::chargeCreeper );

	static {
		ON_SPAWNED.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) );
		ON_SPAWNED.addCondition( new CustomConditions.CRDChance( 0.125 ) );
		ON_SPAWNED.addCondition( new Condition.Excludable() );
		ON_SPAWNED.addCondition( new Condition.ContextOnSpawned( data->data.target instanceof Creeper && data.level != null ) );
	}

	public CreeperSpawnCharged() {
		super( GameModifier.DEFAULT, "CreeperSpawnCharged", "Creeper may spawn charged.", ON_SPAWNED );
	}

	private static void chargeCreeper( com.mlib.gamemodifiers.GameModifier gameModifier, OnSpawnedContext.Data data ) {
		assert data.level != null;
		Creeper creeper = ( Creeper )data.target;
		LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create( data.level );
		if( lightningBolt != null ) {
			creeper.thunderHit( data.level, lightningBolt );
			creeper.clearFire();
		}
	}
}
