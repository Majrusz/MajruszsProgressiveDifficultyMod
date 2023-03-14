package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.entities.CreeperlingEntity;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnExplosionDetonate;

@AutoInstance
public class CreeperlingsCannotDestroyBlocks {
	public CreeperlingsCannotDestroyBlocks() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "CreeperlingsCannotDestroyBlocks" )
			.comment( "Make the Creeperling do not destroy block on explosion." );

		OnExplosionDetonate.listen( this::revertBlocksToDestroy )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.predicate( data->data.explosion.getExploder() instanceof CreeperlingEntity ) )
			.insertTo( group );
	}

	private void revertBlocksToDestroy( OnExplosionDetonate.Data data ) {
		data.explosion.clearToBlow();
	}
}
