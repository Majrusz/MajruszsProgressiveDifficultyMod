package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamestage.GameStage;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnSpawned;
import net.minecraft.world.entity.animal.Rabbit;

@AutoInstance
public class SpawnKillerBunny {
	public SpawnKillerBunny() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "SpawnKillerBunny" )
			.comment( "Replaces rabbits with the Killer Bunny variant." );

		OnSpawned.listenSafe( this::transformToKillerBunny )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.EXPERT ) )
			.addCondition( Condition.chanceCRD( 0.1, true ) )
			.addCondition( Condition.isServer() )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.predicate( data->data.target instanceof Rabbit rabbit && !rabbit.isBaby() ) )
			.insertTo( group );
	}

	private void transformToKillerBunny( OnSpawned.Data data ) {
		Rabbit rabbit = ( Rabbit )data.target;
		rabbit.setVariant( Rabbit.Variant.EVIL );
	}
}
