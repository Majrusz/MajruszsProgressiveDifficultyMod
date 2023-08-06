package com.majruszsdifficulty.features;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.config.GameStageStringListConfig;
import com.mlib.Utility;
import com.mlib.config.ConfigGroup;
import com.mlib.contexts.OnCheckSpawn;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.modhelper.AutoInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;

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
			.comment( "Blocks certain mobs from spawning when given game stage is active (it only affects natural spawns)." )
			.addConfig( this.forbiddenEntities );

		OnCheckSpawn.listen( OnCheckSpawn.CANCEL )
			.addCondition( Condition.predicate( data->data.getSpawnType() == MobSpawnType.NATURAL ) )
			.addCondition( Condition.predicate( data->this.isBlocked( data.mob ) ) )
			.insertTo( group );
	}

	private boolean isBlocked( Entity entity ) {
		return this.forbiddenEntities.getCurrentGameStageValue().contains( Utility.getRegistryString( entity.getType() ) );
	}
}
