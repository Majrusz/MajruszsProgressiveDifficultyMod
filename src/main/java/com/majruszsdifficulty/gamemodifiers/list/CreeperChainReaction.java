package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnDamaged;
import net.minecraft.world.entity.monster.Creeper;

@AutoInstance
public class CreeperChainReaction {
	public CreeperChainReaction() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "CreeperChainReaction" )
			.comment( "Makes a Creeper ignite when any other Creeper explodes nearby." );

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
