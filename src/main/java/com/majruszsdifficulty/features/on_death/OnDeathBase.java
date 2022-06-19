package com.majruszsdifficulty.features.on_death;

import com.majruszsdifficulty.GameState;
import com.majruszsdifficulty.features.ChanceFeatureBase;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/** Class with common code of all 'OnDeath' classes. */
public abstract class OnDeathBase extends ChanceFeatureBase implements IOnDeath {
	public OnDeathBase( String configName, String configComment, double defaultChance, GameState.State minimumState, boolean shouldChanceBeMultipliedByCRD
	) {
		super( configName, configComment, defaultChance, minimumState, shouldChanceBeMultipliedByCRD );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return tryChance( target ) && isEnabled() && GameState.atLeast( this.minimumState ) && target.level instanceof ServerLevel;
	}
}
