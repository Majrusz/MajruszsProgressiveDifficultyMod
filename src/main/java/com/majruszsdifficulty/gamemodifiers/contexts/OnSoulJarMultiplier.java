package com.majruszsdifficulty.gamemodifiers.contexts;

import com.mlib.gamemodifiers.Context;
import com.mlib.gamemodifiers.Contexts;
import com.mlib.gamemodifiers.data.ILevelData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class OnSoulJarMultiplier {
	public static Context< Data > listen( Consumer< Data > consumer ) {
		return Contexts.get( Data.class ).add( consumer );
	}

	public static Data dispatch( @Nullable Entity entity, ItemStack itemStack ) {
		return Contexts.get( Data.class ).dispatch( new Data( entity, itemStack ) );
	}

	public static class Data implements ILevelData {
		@Nullable public final Entity entity;
		public final ItemStack itemStack;
		public float multiplier = 1.0f;

		public Data( @Nullable Entity entity, ItemStack itemStack ) {
			this.entity = entity;
			this.itemStack = itemStack;
		}

		public float getMultiplier() {
			return Math.max( this.multiplier, 0.0f );
		}

		@Override
		public Level getLevel() {
			return this.entity != null ? this.entity.level() : null;
		}
	}
}
