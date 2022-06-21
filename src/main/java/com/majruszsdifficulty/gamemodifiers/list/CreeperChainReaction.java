package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.ICondition;
import com.majruszsdifficulty.gamemodifiers.contexts.DamagedContext;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperChainReaction extends GameModifier {
	static final ICondition.Excludable EXCLUDABLE = new ICondition.Excludable();
	static final ICondition.GameStage GAME_STAGE = new ICondition.GameStage( GameStage.Stage.EXPERT );
	static final ICondition.Context< ? > ARE_CREEPERS = new ICondition.Context<>( DamagedContext.Data.class, data->data.target instanceof Creeper && data.attacker instanceof Creeper );
	static final DamagedContext ON_DAMAGED = new DamagedContext( EXCLUDABLE, GAME_STAGE, ARE_CREEPERS );

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
