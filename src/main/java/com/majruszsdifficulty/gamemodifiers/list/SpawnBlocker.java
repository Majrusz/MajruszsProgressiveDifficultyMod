package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.config.GameStageStringListConfig;
import com.mlib.Utility;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnCheckSpawn;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;

@AutoInstance
public class SpawnBlocker extends GameModifier {
	final GameStageStringListConfig forbiddenEntities = new GameStageStringListConfig( new String[]{
		"minecraft:illusioner",
		"majruszsdifficulty:tank"
	}, new String[]{}, new String[]{} );

	public SpawnBlocker() {
		super( Registries.Modifiers.DEFAULT );

		OnCheckSpawn.listen( this::blockSpawn )
			.addCondition( Condition.predicate( data->this.isForbidden( data.mob ) ) )
			.addConfig( this.forbiddenEntities )
			.insertTo( this );

		this.name( "SpawnBlocker" ).comment( "Blocks certain mobs from spawning when given game stage is active." );
	}

	private void blockSpawn( OnCheckSpawn.Data data ) {
		data.event.setResult( Event.Result.DENY );
	}

	private boolean isForbidden( Entity entity ) {
		return this.forbiddenEntities.getCurrentGameStageValue().contains( Utility.getRegistryString( entity.getType() ) );
	}
}
