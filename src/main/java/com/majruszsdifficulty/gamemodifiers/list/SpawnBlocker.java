package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.config.GameStageStringListConfig;
import com.mlib.Utility;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnCheckSpawn;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;

public class SpawnBlocker extends GameModifier {
	final GameStageStringListConfig forbiddenEntities = new GameStageStringListConfig( "", "", new String[]{
		"minecraft:illusioner", "majruszsdifficulty:tank"
	}, new String[]{}, new String[]{} );

	public SpawnBlocker() {
		super( Registries.Modifiers.DEFAULT, "SpawnBlocker", "Blocks certain mobs from spawning when given game stage is active." );

		OnCheckSpawn.Context onCheckSpawn = new OnCheckSpawn.Context( this::blockSpawn );
		onCheckSpawn.addCondition( data->this.isForbidden( data.entity ) )
			.addConfig( this.forbiddenEntities );

		this.addContext( onCheckSpawn );
	}

	private void blockSpawn( OnCheckSpawn.Data data ) {
		data.event.setResult( Event.Result.DENY );
	}

	private boolean isForbidden( Entity entity ) {
		return this.forbiddenEntities.getCurrentGameStageValue().contains( Utility.getRegistryString( entity.getType() ) );
	}
}
