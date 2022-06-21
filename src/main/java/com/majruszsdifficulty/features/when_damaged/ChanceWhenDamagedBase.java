package com.majruszsdifficulty.features.when_damaged;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.features.ChanceFeatureBase;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/** Base class representing event on which entity was damaged that has certain chance to happen. */
public abstract class ChanceWhenDamagedBase extends ChanceFeatureBase implements IWhenDamaged {
	public ChanceWhenDamagedBase( String configName, String configComment, double defaultChance, GameStage.Stage minimumStage,
		boolean shouldChanceBeMultipliedByCRD
	) {
		super( configName, configComment, defaultChance, minimumStage, shouldChanceBeMultipliedByCRD );
	}

	/** Checks if all conditions were met. */
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return isEnabled() && GameStage.atLeast( this.minimumStage ) && target.level instanceof ServerLevel;
	}
}