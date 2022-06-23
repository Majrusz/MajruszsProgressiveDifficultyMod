package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperSpawnCharged extends GameModifier {
	static final OnSpawnedContext ON_SPAWNED = new OnSpawnedContext();

	static {
		ON_SPAWNED.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) );
		ON_SPAWNED.addCondition( new CustomConditions.CRDChance( 0.125 ) );
		ON_SPAWNED.addCondition( new Condition.Excludable() );
		ON_SPAWNED.addCondition( new Condition.Context<>( OnSpawnedContext.Data.class, data->data.target instanceof Creeper ) );
	}

	public CreeperSpawnCharged() {
		super( GameModifier.DEFAULT, "CreeperSpawnCharged", "Creeper may spawn charged.", ON_SPAWNED );
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
