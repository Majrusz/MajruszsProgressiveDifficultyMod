package com.majruszs_difficulty.features.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.features.FeatureBase;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/** Base class representing event on which entity was damaged. */
public abstract class WhenDamagedBase extends FeatureBase implements IWhenDamaged {
	public WhenDamagedBase( String configName, String configComment, GameState.State minimumState ) {
		super( configName, configComment, minimumState );
	}

	/** Checks if all conditions were met. */
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return isEnabled() && GameState.atLeast( this.minimumState ) && target.level instanceof ServerLevel;
	}
}