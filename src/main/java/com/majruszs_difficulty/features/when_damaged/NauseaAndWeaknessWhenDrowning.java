package com.majruszs_difficulty.features.when_damaged;

import com.majruszs_difficulty.GameState;
import com.mlib.TimeConverter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;

import javax.annotation.Nullable;

/** Applying blindness when player is drowning. */
public class NauseaAndWeaknessWhenDrowning extends WhenDamagedApplyStackableEffectBase {
	private static final String CONFIG_NAME = "NauseaAndWeaknessWhenDrowning";
	private static final String CONFIG_COMMENT = "Drowning inflicts Nausea and Drowning.";

	public NauseaAndWeaknessWhenDrowning() {
		super( CONFIG_NAME, CONFIG_COMMENT, 1.0, 5.0, GameState.State.NORMAL, false, new Effect[]{ Effects.NAUSEA, Effects.WEAKNESS }, false, true, 0,
			TimeConverter.secondsToTicks( 10.0 )
		);
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return damageSource.equals( DamageSource.DROWN ) && super.shouldBeExecuted( attacker, target, damageSource );
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return 0;
	}
}
