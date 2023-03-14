package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.goals.CreeperExplodeWallsGoal;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnSpawned;
import net.minecraft.world.entity.monster.Creeper;

@AutoInstance
public class CreeperExplodeBehindWall extends GameModifier {
	public CreeperExplodeBehindWall() {
		super( Registries.Modifiers.DEFAULT );

		OnSpawned.listenSafe( this::addNewGoal )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.EXPERT ) )
			.addCondition( Condition.chanceCRD( 1.0, false ) )
			.addCondition( Condition.isServer() )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.predicate( data->data.target instanceof Creeper ) )
			.insertTo( this );

		this.name( "CreeperExplodeBehindWall" ).comment( "Creeper explodes when the player is behind the wall." );
	}

	private void addNewGoal( OnSpawned.Data data ) {
		Creeper creeper = ( Creeper )data.target;
		creeper.goalSelector.addGoal( 1, new CreeperExplodeWallsGoal( creeper ) );
	}
}
