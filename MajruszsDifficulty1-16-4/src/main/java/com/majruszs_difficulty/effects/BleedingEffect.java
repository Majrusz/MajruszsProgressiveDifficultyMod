package com.majruszs_difficulty.effects;

import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;

import javax.annotation.Nullable;

/** Bleeding effect similar to poison effect. */
public class BleedingEffect extends Effect {
	public static final BleedingEffect instance = new BleedingEffect();

	public BleedingEffect() {
		super( EffectType.HARMFUL, 0xffdd5555 );
	}

	/** Called every time when effect 'isReady'. */
	@Override
	public void performEffect( LivingEntity entity, int amplifier ) {
		entity.attackEntityFrom( DamageSource.MAGIC, 1.0f );
	}

	/** When effect starts bleeding will not do anything. */
	@Override
	public void affectEntity( @Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity, int amplifier, double health ) {}

	/** Calculating whether effect is ready to deal damage. */
	@Override
	public boolean isReady( int duration, int amplifier ) {
		int cooldown = Math.max( 5, MajruszsHelper.secondsToTicks( 3.0 ) >> amplifier );

		return duration % cooldown == 0;
	}
}
