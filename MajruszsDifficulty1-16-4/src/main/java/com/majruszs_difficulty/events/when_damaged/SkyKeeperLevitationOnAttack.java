package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.ConfigHandler.Config;
import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.entities.SkyKeeperEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;

import javax.annotation.Nullable;

/** Making Sky Keeper attacks have a chance to set levitation on enemies. */
public class SkyKeeperLevitationOnAttack extends WhenDamagedApplyStackableEffectBase {
	public SkyKeeperLevitationOnAttack() {
		super( GameState.State.NORMAL, true, Effects.LEVITATION, false, true, 1, MajruszsHelper.secondsToTicks( 60.0 ) );
	}

	/** Checking if all conditions were met. */
	@Override
	protected boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return attacker instanceof SkyKeeperEntity && super.shouldBeExecuted( attacker, target, damageSource );
	}

	@Override
	protected boolean isEnabled() {
		return !Config.isDisabled( Config.Features.SKY_KEEPER_LEVITATION );
	}

	@Override
	protected double getChance() {
		return Config.getChance( Config.Chances.SKY_KEEPER_LEVITATION );
	}

	@Override
	protected int getDurationInTicks( Difficulty difficulty ) {
		return Config.getDurationInSeconds( Config.Durations.SKY_KEEPER_LEVITATION );
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return 0;
	}
}
