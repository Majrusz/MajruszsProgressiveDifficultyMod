package com.majruszsdifficulty.features.on_death;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.features.ChanceFeatureBaseOld;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/** Class with common code of all 'OnDeath' classes. */
public abstract class OnDeathBaseOld extends ChanceFeatureBaseOld implements IOnDeath {
	public OnDeathBaseOld( String configName, String configComment, double defaultChance, GameStage.Stage minimumStage, boolean shouldChanceBeMultipliedByCRD
	) {
		super( configName, configComment, defaultChance, minimumStage, shouldChanceBeMultipliedByCRD );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return tryChance( target ) && isEnabled() && GameStage.atLeast( this.minimumStage ) && target.level instanceof ServerLevel;
	}
}
