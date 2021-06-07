package com.majruszs_difficulty.events.on_death;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.events.ChanceFeatureBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

/** Class with common code of all 'OnDeath' classes. */
public abstract class OnDeathBase extends ChanceFeatureBase implements IOnDeath {
	public OnDeathBase( String configName, String configComment, double defaultChance, GameState.State minimumState,
		boolean shouldChanceBeMultipliedByCRD
	) {
		super( configName, configComment, defaultChance, minimumState, shouldChanceBeMultipliedByCRD );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return tryChance( target ) && target.world instanceof ServerWorld;
	}
}
