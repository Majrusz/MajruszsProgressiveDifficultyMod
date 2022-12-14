package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperChainReaction extends GameModifier {
	public CreeperChainReaction() {
		super( Registries.Modifiers.DEFAULT, "CreeperChainReaction", "Makes a Creeper ignite once any other Creeper explode nearby." );

		OnDamaged.Context onDamaged = new OnDamaged.Context( this::igniteCreeper );
		onDamaged.addCondition( new CustomConditions.GameStage<>( GameStage.Stage.EXPERT ) )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( data->data.target instanceof Creeper && data.attacker instanceof Creeper );

		this.addContext( onDamaged );
	}

	private void igniteCreeper( OnDamaged.Data data ) {
		Creeper creeper = ( Creeper )data.target;
		creeper.ignite();
	}
}
