package com.majruszsdifficulty.effects;

import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class BleedingEffect extends MobEffect {
	public static boolean isEnabled() {
		return MajruszsDifficulty.CONFIG.bleeding.isEnabled;
	}

	public BleedingEffect() {
		super( MobEffectCategory.HARMFUL, 0xffdd5555 );
	}

	@Override
	public void applyEffectTick( LivingEntity entity, int amplifier ) {}

	@Override
	public void applyInstantenousEffect( @Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity, int amplifier, double health ) {}

	@Override
	public boolean isDurationEffectTick( int duration, int amplifier ) {
		return false;
	}

	/** Bleeding effect instance that stores information about the causer of bleeding. (required for converting villager to zombie villager etc.) */
	public static class MobEffectInstance extends net.minecraft.world.effect.MobEffectInstance {
		public final @Nullable Entity damageSourceEntity;

		public MobEffectInstance( int duration, int amplifier, boolean ambient, @Nullable LivingEntity attacker ) {
			super( MajruszsDifficulty.BLEEDING.get(), duration, amplifier, ambient, false, true );

			this.damageSourceEntity = attacker;
		}
	}
}
