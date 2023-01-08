package com.majruszsdifficulty.gamemodifiers.contexts;

import com.mlib.gamemodifiers.ContextBase;
import com.mlib.gamemodifiers.Contexts;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.function.Consumer;

public class OnBleedingCheck {
	public static class Context extends ContextBase< Data > {
		static final Contexts< Data, Context > CONTEXTS = new Contexts<>();

		public static void accept( Data data ) {
			CONTEXTS.accept( data );
		}

		public Context( Consumer< Data > consumer ) {
			super( consumer );

			CONTEXTS.add( this );
		}
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
