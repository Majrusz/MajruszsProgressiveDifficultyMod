package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsHelper;
import com.mlib.config.DurationConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;

/** Base class representing event on which enemies will receive some effects after being attacked. */
public abstract class WhenDamagedApplyEffectBase extends WhenDamagedBase {
	protected final Effect[] effects;
	protected final DurationConfig effectDuration;

	public WhenDamagedApplyEffectBase( String configName, String configComment, double defaultChance, double defaultDurationInSeconds,
		GameState.State minimumState, boolean shouldBeMultipliedByCRD, Effect[] effects
	) {
		super( configName, configComment, defaultChance, minimumState, shouldBeMultipliedByCRD );
		this.effects = effects;

		String comment = "Effect" + ( effects.length > 1 ? "s" : "" ) + " duration in seconds.";
		this.effectDuration = new DurationConfig( "duration", comment, false, defaultDurationInSeconds, 1.0, 600.0 );

		if( defaultDurationInSeconds != -1.0 )
			this.whenDamagedGroup.addConfig( this.effectDuration );
	}

	public WhenDamagedApplyEffectBase( String configName, String configComment, double defaultChance, double defaultDurationInSeconds,
		GameState.State minimumState, boolean shouldBeMultipliedByCRD, Effect effect
	) {
		this( configName, configComment, defaultChance, defaultDurationInSeconds, minimumState, shouldBeMultipliedByCRD, new Effect[]{ effect } );
	}

	/**
	 Function called when entity was damaged.

	 @param target Entity target that was attacked.
	 */
	@Override
	public void whenDamaged( LivingEntity target ) {
		ServerWorld world = ( ServerWorld )target.getEntityWorld();
		Difficulty difficulty = world.getDifficulty();

		for( Effect effect : this.effects ) {
			if( !MajruszsHelper.tryChance( calculateChance( target ) ) )
				continue;

			applyEffect( target, effect, difficulty );
		}
	}

	/**
	 Applying effect on entity directly. (if possible, because enemy may be immune for example)

	 @param target     Entity who will get effect.
	 @param effect     Effect type to apply.
	 @param difficulty Current world difficulty.
	 */
	protected void applyEffect( LivingEntity target, Effect effect, Difficulty difficulty ) {
		MajruszsHelper.applyEffectIfPossible( target, effect, getDurationInTicks( difficulty ), getAmplifier( difficulty ) );
	}

	/**
	 Returns the duration in ticks of the effect.

	 @param difficulty Current game difficulty. (peaceful, easy, normal, hard)
	 */
	protected int getDurationInTicks( Difficulty difficulty ) {
		return this.effectDuration.getDuration();
	}

	/**
	 Returns the level of the effect.

	 @param difficulty Current game difficulty. (peaceful, easy, normal, hard)
	 */
	protected abstract int getAmplifier( Difficulty difficulty );
}
