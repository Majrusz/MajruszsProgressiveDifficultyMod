package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.ICondition;
import com.majruszsdifficulty.gamemodifiers.contexts.DamagedContext;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperChainReaction extends GameModifier {
	static final DamagedContext ON_DAMAGED = new DamagedContext();

	static {
		ON_DAMAGED.addCondition( new ICondition.Excludable() );
		ON_DAMAGED.addCondition( new ICondition.GameStage( GameStage.Stage.EXPERT ) );
		ON_DAMAGED.addCondition( new ICondition.Context<>( DamagedContext.Data.class, data->data.target instanceof Creeper && data.attacker instanceof Creeper ) );
	}

	public CreeperChainReaction() {
		super( "CreeperChainReaction", "Makes a Creeper ignite once any other Creeper explode nearby.", ON_DAMAGED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof DamagedContext.Data damagedData ) {
			Creeper creeper = ( Creeper )damagedData.target;
			creeper.ignite();
		}
	}
}
