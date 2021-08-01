package com.majruszs_difficulty.features.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.entities.SkyKeeperEntity;
import com.mlib.TimeConverter;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/** Making Sky Keeper attacks have a chance to set levitation on enemies. */
public class SkyKeeperLevitationOnAttack extends WhenDamagedApplyStackableEffectBase {
	private static final String CONFIG_NAME = "SkyKeeperLevitation";
	private static final String CONFIG_COMMENT = "Sky Keeper inflicts levitation.";

	public SkyKeeperLevitationOnAttack() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.5, 6.0, GameState.State.NORMAL, true, MobEffects.LEVITATION, false, true, 1,
			TimeConverter.secondsToTicks( 60.0 )
		);
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return attacker instanceof SkyKeeperEntity && super.shouldBeExecuted( attacker, target, damageSource );
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return 0;
	}
}
