package com.majruszsdifficulty.events;

import com.majruszlibrary.events.OnEntityDamaged;
import com.majruszlibrary.events.base.Event;
import com.majruszlibrary.events.base.Events;
import com.majruszlibrary.events.type.ICancellableEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class OnBleedingCheck implements ICancellableEvent {
	public final DamageSource source;
	public final @Nullable LivingEntity attacker;
	public final LivingEntity target;
	private boolean isBleedingTriggered = false;

	public static Event< OnBleedingCheck > listen( Consumer< OnBleedingCheck > consumer ) {
		return Events.get( OnBleedingCheck.class ).add( consumer );
	}

	public OnBleedingCheck( OnEntityDamaged data ) {
		this.source = data.source;
		this.attacker = data.attacker;
		this.target = data.target;
	}

	@Override
	public boolean isExecutionStopped() {
		return this.isBleedingTriggered();
	}

	public void trigger() {
		this.isBleedingTriggered = true;
	}

	public boolean isBleedingTriggered() {
		return this.isBleedingTriggered;
	}
}
