package com.majruszs_difficulty.features.when_damaged;

import com.majruszs_difficulty.Instances;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/** Making Cactus inflict bleeding on enemies. */
public class CactusBleedingOnHurt extends WhenDamagedApplyBleedingBase {
	private static final String CONFIG_NAME = "CactusBleeding";
	private static final String CONFIG_COMMENT = "Touching cactus inflict bleeding.";

	public CactusBleedingOnHurt() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.5, 24.0 );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return damageSource.equals( DamageSource.CACTUS ) && super.shouldBeExecuted( attacker, target, damageSource );
	}

	@Override
	protected void applyEffect( @Nullable LivingEntity attacker, LivingEntity target, MobEffect effect, Difficulty difficulty ) {
		super.applyEffect( attacker, target, effect, difficulty );

		if( target instanceof ServerPlayer )
			Instances.BASIC_TRIGGER.trigger( ( ServerPlayer )target, "cactus_bleeding" );
	}
}