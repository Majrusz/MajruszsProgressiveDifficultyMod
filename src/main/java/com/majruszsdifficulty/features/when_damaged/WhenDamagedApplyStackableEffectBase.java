package com.majruszsdifficulty.features.when_damaged;

import com.majruszsdifficulty.GameState;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/** Base class representing event on which enemies will receive some stackable effects after being attacked. */
public abstract class WhenDamagedApplyStackableEffectBase extends WhenDamagedApplyEffectBase {
	protected final boolean isAmplifierStackable;
	protected final boolean isDurationStackable;
	protected final int maximumAmplifier;
	protected final int maximumDurationInTicks;

	public WhenDamagedApplyStackableEffectBase( String configName, String configComment, double defaultChance, double defaultDurationInSeconds, GameState.State minimumState,
		boolean shouldBeMultipliedByCRD, MobEffect[] effects, boolean isAmplifierStackable, boolean isDurationStackable, int maximumAmplifier, int maximumDurationInTicks
	) {
		super( configName, configComment, defaultChance, defaultDurationInSeconds, minimumState, shouldBeMultipliedByCRD, effects );

		this.isAmplifierStackable = isAmplifierStackable;
		this.isDurationStackable = isDurationStackable;
		this.maximumAmplifier = maximumAmplifier;
		this.maximumDurationInTicks = maximumDurationInTicks;
	}

	public WhenDamagedApplyStackableEffectBase( String configName, String configComment, double defaultChance, double defaultDurationInSeconds, GameState.State minimumState,
		boolean shouldBeMultipliedByCRD, MobEffect effect, boolean isAmplifierStackable, boolean isDurationStackable, int maximumAmplifier, int maximumDurationInTicks
	) {
		this( configName, configComment, defaultChance, defaultDurationInSeconds, minimumState, shouldBeMultipliedByCRD, new MobEffect[]{ effect }, isAmplifierStackable, isDurationStackable,
			maximumAmplifier, maximumDurationInTicks
		);
	}

	/**
	 Applying effect on entity directly. (if possible, because enemy may be immune for example)

	 @param target     Entity who will get effect.
	 @param effect     MobEffect type to apply.
	 @param difficulty Current world difficulty.
	 */
	@Override
	protected void applyEffect( @Nullable LivingEntity attacker, LivingEntity target, MobEffect effect, Difficulty difficulty ) {
		MobEffectInstance previousMobEffectInstance = target.getEffect( effect );

		if( previousMobEffectInstance == null ) {
			super.applyEffect( attacker, target, effect, difficulty );
			return;
		}

		int durationInTicks = getDurationInTicks( difficulty );
		if( this.isDurationStackable )
			durationInTicks = Math.min( durationInTicks + previousMobEffectInstance.getDuration(), this.maximumDurationInTicks );

		int amplifier = getAmplifier( difficulty );
		if( this.isAmplifierStackable )
			amplifier = Math.min( amplifier + previousMobEffectInstance.getAmplifier() + 1, this.maximumAmplifier );

		MobEffectInstance effectInstance = new MobEffectInstance( effect, durationInTicks, amplifier );
		if( target.canBeAffected( effectInstance ) )
			target.addEffect( effectInstance );
	}
}
