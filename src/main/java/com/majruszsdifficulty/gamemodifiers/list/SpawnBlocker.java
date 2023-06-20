package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.config.GameStageStringListConfig;
import com.mlib.Utility;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnCheckSpawn;
import net.minecraft.world.entity.Entity;

@AutoInstance
public class SpawnBlocker {
	final GameStageStringListConfig forbiddenEntities = new GameStageStringListConfig( new String[]{
		"minecraft:illusioner",
		"majruszsdifficulty:tank",
		"majruszsdifficulty:cerberus"
	}, new String[]{ "majruszsdifficulty:cerberus" }, new String[]{} );

	public SpawnBlocker() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "SpawnBlocker" )
			.comment( "Blocks certain mobs from spawning when given game stage is active." );

		OnCheckSpawn.listen( OnCheckSpawn.CANCEL )
			.addCondition( Condition.predicate( data->this.isForbidden( data.mob ) ) )
			.addConfig( this.forbiddenEntities )
			.insertTo( group );
	}

	private boolean isForbidden( Entity entity ) {
		return this.forbiddenEntities.getCurrentGameStageValue().contains( Utility.getRegistryString( entity.getType() ) );
	}
}
