package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.goals.CreeperExplodeWallsGoal;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperExplodeBehindWall extends GameModifier {
	static final OnSpawnedContext ON_SPAWNED = new OnSpawnedContext( CreeperExplodeBehindWall::addNewGoal );

	static {
		ON_SPAWNED.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) );
		ON_SPAWNED.addCondition( new Condition.Chance( 1.0 ) );
		ON_SPAWNED.addCondition( new Condition.Excludable() );
		ON_SPAWNED.addCondition( new Condition.ContextOnSpawned( data->data.target instanceof Creeper ) );
	}

	public CreeperExplodeBehindWall() {
		super( GameModifier.DEFAULT, "CreeperExplodeBehindWall", "Creeper explodes when the player is behind the wall.", ON_SPAWNED );
	}

	private static void addNewGoal( com.mlib.gamemodifiers.GameModifier gameModifier, OnSpawnedContext.Data data ) {
		Creeper creeper = ( Creeper )data.target;
		creeper.goalSelector.addGoal( 1, new CreeperExplodeWallsGoal( creeper ) );
	}
}
