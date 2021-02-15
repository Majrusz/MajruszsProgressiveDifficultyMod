package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.events.FeatureBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;


/** Base class representing event on which entity was damaged. */
public abstract class WhenDamagedBase extends FeatureBase {
	public WhenDamagedBase( String configName, String configComment, double defaultChance, GameState.State minimumState,
		boolean shouldChanceBeMultipliedByCRD
	) {
		super( configName, configComment, defaultChance, minimumState, shouldChanceBeMultipliedByCRD );
	}

	/**
	 Function called when entity was damaged.

	 @param target Entity target that was attacked.
	 */
	public abstract void whenDamaged( @Nullable LivingEntity attacker, LivingEntity target, float damage );

	/** Checking if all conditions were met. */
	protected boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		if( !GameState.atLeast( this.minimumState ) )
			return false;

		if( !( target.world instanceof ServerWorld ) )
			return false;

		return isEnabled();
	}
}