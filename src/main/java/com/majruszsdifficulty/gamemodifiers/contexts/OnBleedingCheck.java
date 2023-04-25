package com.majruszsdifficulty.gamemodifiers.contexts;

import com.mlib.gamemodifiers.Context;
import com.mlib.gamemodifiers.Contexts;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.function.Consumer;

public class OnBleedingCheck {
	public static Context< Data > listen( Consumer< Data > consumer ) {
		return Contexts.get( Data.class ).add( consumer );
	}

	public static Data dispatch( LivingHurtEvent event ) {
		return Contexts.get( Data.class ).dispatch( new Data( event ) );
	}

	public static class Data extends OnDamaged.Data {
		boolean isTriggered = false;

		public Data( LivingHurtEvent event ) {
			super( event );
		}

		public void trigger() {
			this.isTriggered = true;
		}

		public boolean isEffectTriggered() {
			return this.isTriggered;
		}
	}
}
