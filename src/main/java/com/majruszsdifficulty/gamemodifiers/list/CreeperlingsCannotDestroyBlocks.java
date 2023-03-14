package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.entities.CreeperlingEntity;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnExplosionDetonate;

@AutoInstance
public class CreeperlingsCannotDestroyBlocks extends GameModifier {
	public CreeperlingsCannotDestroyBlocks() {
		super( Registries.Modifiers.DEFAULT );

		OnExplosionDetonate.listen( this::revertBlocksToDestroy )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.predicate( data->data.explosion.getExploder() instanceof CreeperlingEntity ) )
			.insertTo( this );

		this.name( "CreeperlingsCannotDestroyBlocks" ).comment( "Make the Creeperling do not destroy block on explosion." );
	}

	private void revertBlocksToDestroy( OnExplosionDetonate.Data data ) {
		data.explosion.clearToBlow();
	}
}
