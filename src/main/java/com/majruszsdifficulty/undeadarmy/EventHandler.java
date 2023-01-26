package com.majruszsdifficulty.undeadarmy;

import com.majruszsdifficulty.Registries;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnServerTick;
import net.minecraftforge.event.TickEvent;

@AutoInstance
public class EventHandler extends GameModifier {
	public EventHandler() {
		super( Registries.Modifiers.UNDEAD_ARMY );

		new OnServerTick.Context( this::tickManager )
			.addCondition( data->data.event.phase == TickEvent.Phase.END )
			.insertTo( this );
	}

	private void tickManager( OnServerTick.Data data ) {
		Registries.UNDEAD_ARMY_MANAGER.tick();
	}
}
