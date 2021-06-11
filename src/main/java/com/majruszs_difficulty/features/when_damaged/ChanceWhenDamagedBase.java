package com.majruszs_difficulty.features.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.features.ChanceFeatureBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

/** Base class representing event on which entity was damaged that has certain chance to happen. */
public abstract class ChanceWhenDamagedBase extends ChanceFeatureBase implements IWhenDamaged {
	public ChanceWhenDamagedBase( String configName, String configComment, double defaultChance, GameState.State minimumState,
		boolean shouldChanceBeMultipliedByCRD
	) {
		super( configName, configComment, defaultChance, minimumState, shouldChanceBeMultipliedByCRD );
	}

	/** Checks if all conditions were met. */
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return isEnabled() && GameState.atLeast( this.minimumState ) && target.world instanceof ServerWorld;
	}
}