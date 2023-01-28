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

		new OnSpawned.ContextSafe( this::addNewGoal )
			.addCondition( new CustomConditions.GameStage<>( GameStage.Stage.EXPERT ) )
			.addCondition( new CustomConditions.CRDChance<>( 1.0, false ) )
			.addCondition( new Condition.IsServer<>() )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( data->data.target instanceof Creeper )
			.insertTo( this );

		this.name( "CreeperExplodeBehindWall" ).comment( "Creeper explodes when the player is behind the wall." );
	}

	private void addNewGoal( OnSpawned.Data data ) {
		Creeper creeper = ( Creeper )data.target;
		creeper.goalSelector.addGoal( 1, new CreeperExplodeWallsGoal( creeper ) );
	}
}
