package com.majruszsdifficulty.events;

import com.majruszlibrary.events.base.Event;
import com.majruszlibrary.events.base.Events;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class OnSoulJarMultiplierGet {
	public final LivingEntity entity;
	public final ItemStack itemStack;
	public float multiplier = 1.0f;

	public static Event< OnSoulJarMultiplierGet > listen( Consumer< OnSoulJarMultiplierGet > consumer ) {
		return Events.get( OnSoulJarMultiplierGet.class ).add( consumer );
	}

	public OnSoulJarMultiplierGet( LivingEntity entity, ItemStack itemStack ) {
		this.entity = entity;
		this.itemStack = itemStack;
	}

	public float getMultiplier() {
		return Math.max( this.multiplier, 0.0f );
	}
}
