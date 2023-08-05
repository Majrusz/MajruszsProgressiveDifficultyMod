package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.goals.CreeperExplodeWallsGoal;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnSpawned;
import net.minecraft.world.entity.monster.Creeper;

@AutoInstance
public class CreeperExplodeBehindWall {
	public CreeperExplodeBehindWall() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "CreeperExplodeBehindWall" )
			.comment( "Creeper explodes when the player is behind the wall." );

		OnSpawned.listenSafe( this::addNewGoal )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.EXPERT ) )
			.addCondition( Condition.chanceCRD( 1.0, false ) )
			.addCondition( Condition.isServer() )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.predicate( data->data.target instanceof Creeper ) )
			.insertTo( group );
	}

	private void addNewGoal( OnSpawned.Data data ) {
		Creeper creeper = ( Creeper )data.target;
		creeper.goalSelector.addGoal( 1, new CreeperExplodeWallsGoal( creeper ) );
	}
}
