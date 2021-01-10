package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;

import javax.annotation.Nullable;

/** Applying blindness when player is drowning. */
public class NauseaAndWeaknessWhenDrowning extends WhenDamagedApplyStackableEffectBase {
	public NauseaAndWeaknessWhenDrowning() {
		super( GameState.Mode.NORMAL, false, new Effect[]{ Effects.NAUSEA, Effects.WEAKNESS }, false, true, 0, MajruszsHelper.secondsToTicks( 60.0 ) );
	}

	/** Checking if all conditions were met. */
	@Override
	protected boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return damageSource.equals( DamageSource.DROWN ) && super.shouldBeExecuted( attacker, target, damageSource );
	}

	@Override
	protected int getDurationInTicks( Difficulty difficulty ) {
		return MajruszsHelper.secondsToTicks( 10.0 );
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return 0;
	}

	@Override
	protected boolean isEnabled() {
		return true;
	}

	@Override
	protected double getChance() {
		return 1.0;
	}
}
