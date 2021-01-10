package com.majruszs_difficulty.events.attack_effects;

import com.majruszs_difficulty.GameState.Mode;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;

/** Base class representing event on which enemies will receive some effects after being attacked. */
public abstract class OnAttackEffectBase extends OnAttackBase {
	protected final Effect[] effects;

	public OnAttackEffectBase( Class< ? extends LivingEntity > entityCausingEffect, Mode minimumMode, boolean shouldBeMultipliedByCRD, Effect[] effects ) {
		super( entityCausingEffect, minimumMode, shouldBeMultipliedByCRD );
		this.effects = effects;
	}

	public OnAttackEffectBase( Class< ? extends LivingEntity > entityCausingEffect, Mode minimumMode, boolean shouldBeMultipliedByCRD, Effect effect ) {
		this( entityCausingEffect, minimumMode, shouldBeMultipliedByCRD, new Effect[]{ effect } );
	}

	/** Function called when entity attacked second entity. (trying to apply negative effect on target)

	 @param attacker Attacker.
	 @param target Target.
	 @param damageSource Source from which the target was attacked.
	 */
	@Override
	public void onAttack( LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		ServerWorld world = ( ServerWorld )attacker.getEntityWorld();
		Difficulty difficulty = world.getDifficulty();

		for( Effect effect : this.effects ) {
			if( MajruszsHelper.tryChance( calculateChance( target ) ) )
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
