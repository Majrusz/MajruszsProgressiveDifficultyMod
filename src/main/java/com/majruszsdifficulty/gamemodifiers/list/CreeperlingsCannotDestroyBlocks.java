package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.entities.CreeperlingEntity;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.GameModifier;import com.majruszsdifficulty.Registries;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnExplosion;
import net.minecraftforge.event.level.ExplosionEvent;

@AutoInstance
public class CreeperlingsCannotDestroyBlocks extends GameModifier {
	public CreeperlingsCannotDestroyBlocks() {
		super( Registries.Modifiers.DEFAULT, "CreeperlingsCannotDestroyBlocks", "Make the Creeperling do not destroy block on explosion." );

		OnExplosion.Context onExplosion = new OnExplosion.Context( this::revertBlocksToDestroy );
		onExplosion.addCondition( new Condition.Excludable<>() )
			.addCondition( data->data.explosion.getExploder() instanceof CreeperlingEntity )
			.addCondition( data->data.event instanceof ExplosionEvent.Detonate );

		this.addContext( onExplosion );
	}

	private void revertBlocksToDestroy( OnExplosion.Data data ) {
		data.explosion.clearToBlow();
	}
}
