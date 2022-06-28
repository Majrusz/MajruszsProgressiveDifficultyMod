package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.entities.CreeperlingEntity;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnExplosionContext;
import net.minecraftforge.event.world.ExplosionEvent;

public class CreeperlingsCannotDestroyBlocks extends GameModifier {
	static final OnExplosionContext ON_EXPLOSION = new OnExplosionContext( CreeperlingsCannotDestroyBlocks::revertBlocksToDestroy );

	static {
		ON_EXPLOSION.addCondition( new Condition.Excludable() );
		ON_EXPLOSION.addCondition( new Condition.ContextOnExplosion( data->data.explosion.getExploder() instanceof CreeperlingEntity ) );
		ON_EXPLOSION.addCondition( new Condition.ContextOnExplosion( data->data.event instanceof ExplosionEvent.Detonate ) );
	}

	public CreeperlingsCannotDestroyBlocks() {
		super( GameModifier.DEFAULT, "CreeperlingsCannotDestroyBlocks", "Make the Creeperling do not destroy block on explosion.", ON_EXPLOSION );
	}

	private static void revertBlocksToDestroy( com.mlib.gamemodifiers.GameModifier gameModifier, OnExplosionContext.Data data ) {
		data.explosion.clearToBlow();
	}
}
