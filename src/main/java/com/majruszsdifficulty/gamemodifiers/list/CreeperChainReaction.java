package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import net.minecraft.world.entity.monster.Creeper;

@AutoInstance
public class CreeperChainReaction {
	public CreeperChainReaction() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "CreeperChainReaction" )
			.comment( "Makes a Creeper ignite once any other Creeper explode nearby." );

		OnDamaged.listen( this::igniteCreeper )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.EXPERT ) )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.predicate( data->data.target instanceof Creeper && data.attacker instanceof Creeper ) )
			.insertTo( group );
	}

	private void igniteCreeper( OnDamaged.Data data ) {
		Creeper creeper = ( Creeper )data.target;
		creeper.ignite();
	}
}
