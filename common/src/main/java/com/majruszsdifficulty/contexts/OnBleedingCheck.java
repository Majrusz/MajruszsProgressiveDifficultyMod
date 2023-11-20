package com.majruszsdifficulty.contexts;

import com.majruszlibrary.contexts.OnEntityDamaged;
import com.majruszlibrary.contexts.base.Context;
import com.majruszlibrary.contexts.base.Contexts;
import com.majruszlibrary.contexts.data.ICancellableData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class OnBleedingCheck implements ICancellableData {
	public final DamageSource source;
	public final @Nullable LivingEntity attacker;
	public final LivingEntity target;
	private boolean isBleedingTriggered = false;

	public static Context< OnBleedingCheck > listen( Consumer< OnBleedingCheck > consumer ) {
		return Contexts.get( OnBleedingCheck.class ).add( consumer );
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
