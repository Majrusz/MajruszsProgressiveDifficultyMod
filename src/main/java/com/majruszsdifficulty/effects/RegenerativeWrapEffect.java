package com.majruszsdifficulty.effects;

import com.majruszsdifficulty.Registries;
import com.mlib.Utility;
import com.mlib.modhelper.AutoInstance;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.OnDamaged;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class RegenerativeWrapEffect extends MobEffect {
	public RegenerativeWrapEffect() {
		super( MobEffectCategory.BENEFICIAL, 0xffcd5cab );
	}

	@Override
	public void applyEffectTick( LivingEntity entity, int amplifier ) {
		if( entity.getHealth() < entity.getMaxHealth() ) {
			entity.heal( 1.0f );
		}
	}

	@Override
	public void applyInstantenousEffect( @Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity, int amplifier, double health ) {
		this.applyEffectTick( entity, amplifier );
	}

	@Override
	public boolean isDurationEffectTick( int duration, int amplifier ) {
		int cooldown = Utility.secondsToTicks( 8.0 ) >> amplifier;

		return cooldown <= 0 || duration % cooldown == 0;
	}

	@AutoInstance
	public static class RegenerativeWrap {
		public RegenerativeWrap() {
			OnDamaged.listen( this::removeEffect )
				.addCondition( Condition.hasEffect( Registries.REGENERATIVE_WRAP, data->data.target ) )
				.addCondition( OnDamaged.dealtAnyDamage() );
		}

		private void removeEffect( OnDamaged.Data data ) {
			data.target.removeEffect( Registries.REGENERATIVE_WRAP.get() );
		}
	}
}
