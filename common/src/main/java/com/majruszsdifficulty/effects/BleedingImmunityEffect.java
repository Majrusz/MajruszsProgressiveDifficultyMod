package com.majruszsdifficulty.effects;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnEntityEffectCheck;
import com.mlib.entity.EffectHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class BleedingImmunityEffect extends MobEffect {
	public BleedingImmunityEffect() {
		super( MobEffectCategory.BENEFICIAL, 0xff990000 );
	}

	@Override
	public void applyEffectTick( LivingEntity entity, int amplifier ) {}

	@Override
	public void applyInstantenousEffect( @Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity, int amplifier, double health ) {
		entity.removeEffect( MajruszsDifficulty.BLEEDING.get() );
	}

	@Override
	public boolean isDurationEffectTick( int duration, int amplifier ) {
		return false;
	}

	@AutoInstance
	public static class BleedingImmunity {
		public BleedingImmunity() {
			OnEntityEffectCheck.listen( OnEntityEffectCheck::cancelEffect )
				.addCondition( data->EffectHelper.has( MajruszsDifficulty.BLEEDING_IMMUNITY, data.entity ) )
				.addCondition( data->data.effect.equals( MajruszsDifficulty.BLEEDING.get() ) );
		}
	}
}