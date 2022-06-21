package com.majruszsdifficulty.features.when_damaged;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.majruszsdifficulty.effects.BleedingEffect.BleedingMobEffectInstance;
import com.mlib.Utility;
import com.mlib.effects.EffectHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/** Base class representing event on which enemies will receive bleeding after being attacked. */
public abstract class WhenDamagedApplyBleedingBase extends WhenDamagedApplyEffectBase {
	public WhenDamagedApplyBleedingBase( String configName, String configComment, double defaultChance, double defaultDurationInSeconds ) {
		super( configName, configComment, defaultChance, defaultDurationInSeconds, GameStage.Stage.NORMAL, false, Registries.BLEEDING.get() );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return Registries.BLEEDING.get()
			.mayBleed( target ) && !( damageSource instanceof BleedingEffect.EntityBleedingDamageSource ) && super.shouldBeExecuted( attacker, target, damageSource );
	}

	/**
	 Applying effect on entity directly. (if possible, because enemy may be immune for example)

	 @param target     Entity who will get effect.
	 @param effect     MobEffect type to apply.
	 @param difficulty Current world difficulty.
	 */
	@Override
	protected void applyEffect( @Nullable LivingEntity attacker, LivingEntity target, MobEffect effect, Difficulty difficulty ) {
		BleedingMobEffectInstance effectInstance = new BleedingMobEffectInstance( getDurationInTicks( difficulty ), getAmplifier( difficulty ), false, true, attacker );

		EffectHelper.applyEffectIfPossible( target, effectInstance );
		ServerPlayer serverPlayer = Utility.castIfPossible( ServerPlayer.class, attacker );
		if( serverPlayer != null )
			Registries.BASIC_TRIGGER.trigger( serverPlayer, "bleeding_inflicted" );
	}

	/** Applying invisible bleeding effect instead of standard one. */

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return Registries.BLEEDING.get().getAmplifier();
	}

	/** Calculating final chance. (after applying clamped regional difficulty and armor multipliers) */
	@Override
	public double calculateChance( LivingEntity target ) {
		return Registries.BLEEDING.get().calculateBleedChanceMultiplier( target ) * super.calculateChance( target );
	}
}