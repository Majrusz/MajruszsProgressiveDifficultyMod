package com.majruszs_difficulty.features.when_damaged;

import com.majruszs_difficulty.GameState;
import com.mlib.TimeConverter;
import com.mlib.config.AvailabilityConfig;
import com.mlib.effects.EffectHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import javax.annotation.Nullable;

/** Inflicts Nausea and Slowness when player takes damage from falling. */
public class NauseaAndSlownessWhenFalling extends WhenDamagedApplyEffectBase {
	private static final String CONFIG_NAME = "FallEffects";
	private static final String CONFIG_COMMENT = "Applies Nausea and Slowness on fall.";
	protected final AvailabilityConfig nauseaAvailability;
	protected final AvailabilityConfig slownessAvailability;

	public NauseaAndSlownessWhenFalling() {
		super( CONFIG_NAME, CONFIG_COMMENT, 1.0, 10.0, GameState.State.NORMAL, false,
			new MobEffect[]{ MobEffects.CONFUSION, MobEffects.MOVEMENT_SLOWDOWN }
		);

		String nauseaComment = "Is applying Nausea enabled?";
		String slownessComment = "Is applying Slowness enabled?";
		this.nauseaAvailability = new AvailabilityConfig( "nausea", nauseaComment, false, true );
		this.slownessAvailability = new AvailabilityConfig( "slowness", slownessComment, false, true );
		this.featureGroup.addConfigs( this.nauseaAvailability, this.slownessAvailability );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return damageSource.equals( DamageSource.FALL ) && super.shouldBeExecuted( attacker, target, damageSource );
	}

	/** When entity takes fall damage. */
	@Override
	public void whenDamaged( @Nullable LivingEntity attacker, LivingEntity target, LivingHurtEvent event ) {
		float damage = event.getAmount();
		if( damage < 1.5f )
			return;

		ServerLevel world = ( ServerLevel )target.level;
		for( MobEffect effect : this.effects ) {
			if( !tryChance( target ) )
				continue;

			if( effect == MobEffects.MOVEMENT_SLOWDOWN && this.slownessAvailability.isEnabled() ) {
				EffectHelper.applyEffectIfPossible( target, effect,
					getDurationInTicks( world.getDifficulty() ) + TimeConverter.secondsToTicks( damage * 0.5 ), ( int )( damage / 8.0 )
				);
			} else if( effect == MobEffects.CONFUSION && this.nauseaAvailability.isEnabled() ) {
				EffectHelper.applyEffectIfPossible( target, effect, TimeConverter.secondsToTicks( 6.0 ), ( int )( damage / 6.0 ) );
			}
		}
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return 0;
	}
}
