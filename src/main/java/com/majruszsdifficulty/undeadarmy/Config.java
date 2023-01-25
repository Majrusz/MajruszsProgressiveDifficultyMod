package com.majruszsdifficulty.undeadarmy;

import com.majruszsdifficulty.Registries;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.GameModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;

@AutoInstance
public class Config extends GameModifier {
	final ResourceListener resourceListener = new ResourceListener();

	public Config() {
		super( Registries.Modifiers.UNDEAD_ARMY );

		MinecraftForge.EVENT_BUS.addListener( ( AddReloadListenerEvent event )->event.addListener( this.resourceListener ) );
	}
}
