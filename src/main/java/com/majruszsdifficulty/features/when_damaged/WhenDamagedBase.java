package com.majruszsdifficulty.features.when_damaged;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.features.FeatureBase;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/** Base class representing event on which entity was damaged. */
public abstract class WhenDamagedBase extends FeatureBase implements IWhenDamaged {
	public WhenDamagedBase( String configName, String configComment, GameStage.Stage minimumStage ) {
		super( configName, configComment, minimumStage );
	}

	/** Checks if all conditions were met. */
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return isEnabled() && GameStage.atLeast( this.minimumStage ) && target.level instanceof ServerLevel;
	}
}