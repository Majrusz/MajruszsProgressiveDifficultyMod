package com.majruszsdifficulty.features;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.config.GameStageConfig;
import com.mlib.Utility;
import com.mlib.config.ConfigGroup;
import com.mlib.contexts.OnCheckSpawn;
import com.mlib.contexts.OnSpawned;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.modhelper.AutoInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;

import java.util.List;

@AutoInstance
public class SpawnBlocker {
	final GameStageConfig< List< ? extends String > > forbiddenEntities = GameStageConfig.create(
		List.of( "minecraft:illusioner", "majruszsdifficulty:tank", "majruszsdifficulty:cerberus" ),
		List.of( "majruszsdifficulty:cerberus" ),
		List.of()
	);

	public SpawnBlocker() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "SpawnBlocker" )
			.comment( "Makes mobs unable to spawn when given game stage is active (it only affects natural spawns)." )
			.addConfig( this.forbiddenEntities );

		OnCheckSpawn.listen( OnCheckSpawn.CANCEL )
			.addCondition( Condition.predicate( data->data.getSpawnType() == MobSpawnType.NATURAL ) )
			.addCondition( Condition.predicate( data->this.isBlocked( data.mob ) ) )
			.insertTo( group );

		OnSpawned.listen( OnSpawned.CANCEL )
			.addCondition( OnSpawned.isNotLoadedFromDisk() )
			.addCondition( Condition.predicate( data->data.getSpawnType() == MobSpawnType.NATURAL ) )
			.addCondition( Condition.predicate( data->this.isBlocked( data.target ) ) )
			.insertTo( group );
	}

	private boolean isBlocked( Entity entity ) {
		return this.forbiddenEntities.getCurrentGameStageValue().contains( Utility.getRegistryString( entity.getType() ) );
	}
}
