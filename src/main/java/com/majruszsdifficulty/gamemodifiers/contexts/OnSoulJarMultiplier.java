package com.majruszsdifficulty.gamemodifiers.contexts;

import com.mlib.gamemodifiers.ContextBase;
import com.mlib.gamemodifiers.ContextData;
import com.mlib.gamemodifiers.Contexts;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class OnSoulJarMultiplier {
	public static Data broadcast( Data data ) {
		return Context.CONTEXTS.accept( data );
	}

	public static class Context extends ContextBase< Data > {
		static final Contexts< Data, Context > CONTEXTS = new Contexts<>();

		public Context( Consumer< Data > consumer ) {
			super( consumer );

			CONTEXTS.add( this );
		}
	}

	public static class Data extends ContextData {
		public final ItemStack itemStack;
		public float multiplier = 1.0f;

		public Data( @Nullable Entity entity, ItemStack itemStack ) {
			super( entity );

			this.itemStack = itemStack;
		}

		public float getMultiplier() {
			return Math.max( this.multiplier, 0.0f );
		}
	}
}
