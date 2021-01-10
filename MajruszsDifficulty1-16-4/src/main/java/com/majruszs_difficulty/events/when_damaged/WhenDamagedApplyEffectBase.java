package com.majruszs_difficulty.events.when_damaged;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;

public abstract class WhenDamagedApplyEffectBase extends WhenDamagedBase {
	protected final Effect[] effects;

	public WhenDamagedApplyEffectBase( GameState.Mode minimumMode, boolean shouldBeMultipliedByCRD, Effect[] effects ) {
		super( minimumMode, shouldBeMultipliedByCRD );
		this.effects = effects;
	}

	public WhenDamagedApplyEffectBase( GameState.Mode minimumMode, boolean shouldBeMultipliedByCRD, Effect effect ) {
		this( minimumMode, shouldBeMultipliedByCRD, new Effect[]{ effect } );
	}

	/** Function called when entity was damaged.

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
		EffectInstance effectInstance = new EffectInstance( effect, getDurationInTicks( difficulty ), getAmplifier( difficulty ) );
		if( target.isPotionApplicable( effectInstance ) )
			target.addPotionEffect( effectInstance );
	}

	/**
	 Returns the duration in ticks of the effect.

	 @param difficulty Current game difficulty. (peaceful, easy, normal, hard)
	 */
	protected abstract int getDurationInTicks( Difficulty difficulty );

	/**
	 Returns the level of the effect.

	 @param difficulty Current game difficulty. (peaceful, easy, normal, hard)
	 */
	protected abstract int getAmplifier( Difficulty difficulty );
}
