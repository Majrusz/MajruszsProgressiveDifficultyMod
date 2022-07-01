package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.goals.CreeperExplodeWallsGoal;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import com.mlib.gamemodifiers.data.OnSpawnedData;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperExplodeBehindWall extends GameModifier {
	public CreeperExplodeBehindWall() {
		super( GameModifier.DEFAULT, "CreeperExplodeBehindWall", "Creeper explodes when the player is behind the wall." );

		OnSpawnedContext onSpawned = new OnSpawnedContext( this::addNewGoal );
		onSpawned.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) )
			.addCondition( new Condition.Chance( 1.0 ) )
			.addCondition( new Condition.Excludable() )
			.addCondition( new Condition.ContextOnSpawned( data->data.target instanceof Creeper ) );

		this.addContext( onSpawned );
	}

	private void addNewGoal( OnSpawnedData data ) {
		Creeper creeper = ( Creeper )data.target;
		creeper.goalSelector.addGoal( 1, new CreeperExplodeWallsGoal( creeper ) );
	}
}
