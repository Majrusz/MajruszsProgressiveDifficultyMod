package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperChainReaction extends GameModifier {
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext( CreeperChainReaction::igniteCreeper );

	static {
		ON_DAMAGED.addCondition( new CustomConditions.GameStage( GameStage.Stage.EXPERT ) );
		ON_DAMAGED.addCondition( new Condition.Excludable() );
		ON_DAMAGED.addCondition( new Condition.ContextOnDamaged( data->data.target instanceof Creeper && data.attacker instanceof Creeper ) );
	}

	public CreeperChainReaction() {
		super( GameModifier.DEFAULT, "CreeperChainReaction", "Makes a Creeper ignite once any other Creeper explode nearby.", ON_DAMAGED );
	}

	private static void igniteCreeper( com.mlib.gamemodifiers.GameModifier gameModifier, OnDamagedContext.Data data ) {
		Creeper creeper = ( Creeper )data.target;
		creeper.ignite();
	}
}
