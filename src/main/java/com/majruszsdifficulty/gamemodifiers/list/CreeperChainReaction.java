package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import net.minecraft.world.entity.monster.Creeper;

@AutoInstance
public class CreeperChainReaction extends GameModifier {
	public CreeperChainReaction() {
		super( Registries.Modifiers.DEFAULT );

		new OnDamaged.Context( this::igniteCreeper )
			.addCondition( new CustomConditions.GameStage<>( GameStage.EXPERT ) )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( data->data.target instanceof Creeper && data.attacker instanceof Creeper )
			.insertTo( this );

		this.name( "CreeperChainReaction" ).comment( "Makes a Creeper ignite once any other Creeper explode nearby." );
	}

	private void igniteCreeper( OnDamaged.Data data ) {
		Creeper creeper = ( Creeper )data.target;
		creeper.ignite();
	}
}
