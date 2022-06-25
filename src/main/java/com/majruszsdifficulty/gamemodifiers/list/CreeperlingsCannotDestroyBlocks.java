package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.entities.CreeperlingEntity;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnExplosionContext;
import net.minecraftforge.event.world.ExplosionEvent;

public class CreeperlingsCannotDestroyBlocks extends GameModifier {
	static final OnExplosionContext ON_EXPLOSION = new OnExplosionContext();

	static {
		ON_EXPLOSION.addCondition( new Condition.Excludable() );
		ON_EXPLOSION.addCondition( new Condition.ContextOnExplosion( data->data.explosion.getExploder() instanceof CreeperlingEntity ) );
		ON_EXPLOSION.addCondition( new Condition.ContextOnExplosion( data->data.event instanceof ExplosionEvent.Detonate ) );
	}

	public CreeperlingsCannotDestroyBlocks() {
		super( GameModifier.DEFAULT, "CreeperlingsCannotDestroyBlocks", "Make the Creeperling do not destroy block on explosion.", ON_EXPLOSION );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnExplosionContext.Data explosionData ) {
			explosionData.explosion.clearToBlow();
		}
	}
}
