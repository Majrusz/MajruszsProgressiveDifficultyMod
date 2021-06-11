package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.events.FeatureBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

/** Base class representing event on which entity was damaged. */
public abstract class WhenDamagedBase extends FeatureBase implements IWhenDamaged {
	public WhenDamagedBase( String configName, String configComment, GameState.State minimumState ) {
		super( configName, configComment, minimumState );
	}

	/** Checks if all conditions were met. */
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return isEnabled() && GameState.atLeast( this.minimumState ) && target.world instanceof ServerWorld;
	}
}