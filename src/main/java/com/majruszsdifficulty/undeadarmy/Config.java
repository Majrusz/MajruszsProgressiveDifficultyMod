package com.majruszsdifficulty.undeadarmy;

import com.majruszsdifficulty.Registries;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.GameModifier;
import net.minecraft.util.Mth;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;

@AutoInstance
public class Config extends GameModifier {
	private final ResourceListener resourceListener = new ResourceListener();

	public Config() {
		super( Registries.Modifiers.UNDEAD_ARMY );

		MinecraftForge.EVENT_BUS.addListener( ( AddReloadListenerEvent event )->event.addListener( this.resourceListener ) );
	}

	public int getWavesNum() {
		return this.resourceListener.getWavesNum();
	}

	public boolean hasBoss( int waveIdx ) {
		return this.getWave( waveIdx ).boss.get() != null;
	}

	private ResourceListener.Resource.Wave getWave( int waveIdx ) {
		return this.resourceListener.resource.waves.get().get( Mth.clamp( waveIdx - 1, 0, this.getWavesNum() ) );
	}
}
