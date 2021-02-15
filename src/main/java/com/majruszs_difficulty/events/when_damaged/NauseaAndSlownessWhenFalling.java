package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.GameState;
import com.mlib.Random;
import com.mlib.TimeConverter;
import com.mlib.config.AvailabilityConfig;
import com.mlib.effects.EffectHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

/** Inflicts Nausea and Slowness when player takes damage from falling. */
public class NauseaAndSlownessWhenFalling extends WhenDamagedApplyEffectBase {
	private static final String CONFIG_NAME = "FallEffects";
	private static final String CONFIG_COMMENT = "Applies Nausea and Slowness on fall.";
	protected final AvailabilityConfig nauseaAvailability;
	protected final AvailabilityConfig slownessAvailability;

	public NauseaAndSlownessWhenFalling() {
		super( CONFIG_NAME, CONFIG_COMMENT, 1.0, 10.0, GameState.State.NORMAL, false, new Effect[]{ Effects.NAUSEA, Effects.SLOWNESS } );

		String nauseaComment = "Is applying Nausea enabled?";
		String slownessComment = "Is applying Slowness enabled?";
		this.nauseaAvailability = new AvailabilityConfig( "nausea", nauseaComment, false, true );
		this.slownessAvailability = new AvailabilityConfig( "slowness", slownessComment, false, true );
		this.featureGroup.addConfigs( this.nauseaAvailability, this.slownessAvailability );
	}

	/** Checking if all conditions were met. */
	@Override
	protected boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return damageSource.equals( DamageSource.FALL ) && super.shouldBeExecuted( attacker, target, damageSource );
	}

	/** When entity takes fall damage. */
	@Override
	public void whenDamaged( @Nullable LivingEntity attacker, LivingEntity target, float damage ) {
		if( damage < 1.5f )
			return;

		ServerWorld world = ( ServerWorld )target.getEntityWorld();
		for( Effect effect : this.effects ) {
			if( !Random.tryChance( calculateChance( target ) ) )
				continue;

			if( effect == Effects.SLOWNESS && this.slownessAvailability.isEnabled() ) {
				EffectHelper.applyEffectIfPossible( target, effect,
					getDurationInTicks( world.getDifficulty() ) + TimeConverter.secondsToTicks( damage * 0.5 ), ( int )( damage / 8.0 )
				);
			} else if( effect == Effects.NAUSEA && this.nauseaAvailability.isEnabled() ) {
				EffectHelper.applyEffectIfPossible( target, effect, TimeConverter.secondsToTicks( 6.0 ), ( int )( damage / 6.0 ) );
			}
		}
	}

	@Override
	protected int getAmplifier( Difficulty difficulty ) {
		return 0;
	}
}
