package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.ICondition;
import com.majruszsdifficulty.gamemodifiers.contexts.OnDamagedContext;
import com.majruszsdifficulty.gamemodifiers.contexts.OnSpawnedContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperSpawnCharged extends GameModifier {
	static final OnSpawnedContext ON_SPAWNED = new OnSpawnedContext();

	static {
		ON_SPAWNED.addCondition( new ICondition.Excludable() );
		ON_SPAWNED.addCondition( new ICondition.GameStage( GameStage.Stage.NORMAL ) );
		ON_SPAWNED.addCondition( new ICondition.Chance( 0.125, true ) );
		ON_SPAWNED.addCondition( new ICondition.Context<>( OnSpawnedContext.Data.class, data->data.target instanceof Creeper ) );
	}

	public CreeperSpawnCharged() {
		super( "CreeperSpawnCharged", "Creeper may spawn charged.", ON_SPAWNED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnSpawnedContext.Data damagedData && damagedData.level != null ) {
			Creeper creeper = ( Creeper )damagedData.target;
			LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create( damagedData.level );
			if( lightningBolt != null ) {
				creeper.thunderHit( damagedData.level, lightningBolt );
				creeper.clearFire();
			}
		}
	}
}
