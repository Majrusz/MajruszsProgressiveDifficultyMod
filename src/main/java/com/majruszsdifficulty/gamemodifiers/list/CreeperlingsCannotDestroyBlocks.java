package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.entities.CreeperlingEntity;
import com.majruszsdifficulty.gamemodifiers.DifficultyModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnExplosionContext;
import com.mlib.gamemodifiers.data.OnExplosionData;
import net.minecraftforge.event.level.ExplosionEvent;

public class CreeperlingsCannotDestroyBlocks extends DifficultyModifier {
	public CreeperlingsCannotDestroyBlocks() {
		super( DifficultyModifier.DEFAULT, "CreeperlingsCannotDestroyBlocks", "Make the Creeperling do not destroy block on explosion." );

		OnExplosionContext onExplosion = new OnExplosionContext( this::revertBlocksToDestroy );
		onExplosion.addCondition( new Condition.Excludable() )
			.addCondition( data->data.explosion.getExploder() instanceof CreeperlingEntity )
			.addCondition( data->data.event instanceof ExplosionEvent.Detonate );

		this.addContext( onExplosion );
	}

	private void revertBlocksToDestroy( OnExplosionData data ) {
		data.explosion.clearToBlow();
	}
}
