package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.entities.CreeperlingEntity;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnExplosion;
import net.minecraftforge.event.world.ExplosionEvent;

@AutoInstance
public class CreeperlingsCannotDestroyBlocks extends GameModifier {
	public CreeperlingsCannotDestroyBlocks() {
		super( Registries.Modifiers.DEFAULT );

		new OnExplosion.Context( this::revertBlocksToDestroy )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( data->data.explosion.getExploder() instanceof CreeperlingEntity )
			.addCondition( data->data.event instanceof ExplosionEvent.Detonate )
			.insertTo( this );

		this.name( "CreeperlingsCannotDestroyBlocks" ).comment( "Make the Creeperling do not destroy block on explosion." );
	}

	private void revertBlocksToDestroy( OnExplosion.Data data ) {
		data.explosion.clearToBlow();
	}
}
