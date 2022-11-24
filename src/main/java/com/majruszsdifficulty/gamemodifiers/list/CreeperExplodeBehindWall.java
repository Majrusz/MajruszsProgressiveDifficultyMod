package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.gamemodifiers.GameModifier;import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.goals.CreeperExplodeWallsGoal;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnSpawned;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import com.mlib.gamemodifiers.data.OnSpawnedData;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperExplodeBehindWall extends GameModifier {
	public CreeperExplodeBehindWall() {
		super( Registries.Modifiers.DEFAULT, "CreeperExplodeBehindWall", "Creeper explodes when the player is behind the wall." );

		OnSpawned.Context onSpawned = new OnSpawned.Context( this::addNewGoal );
		onSpawned.addCondition( new CustomConditions.GameStage( GameStage.Stage.EXPERT ) )
			.addCondition( new CustomConditions.CRDChance( 1.0, false ) )
			.addCondition( new Condition.Excludable() )
			.addCondition( data->data.target instanceof Creeper );

		this.addContext( onSpawned );
	}

	private void addNewGoal( OnSpawned.Data data ) {
		Creeper creeper = ( Creeper )data.target;
		creeper.goalSelector.addGoal( 1, new CreeperExplodeWallsGoal( creeper ) );
	}
}
