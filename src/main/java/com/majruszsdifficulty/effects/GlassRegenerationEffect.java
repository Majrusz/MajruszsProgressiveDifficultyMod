package com.majruszsdifficulty.effects;

import com.majruszsdifficulty.Registries;
import com.mlib.Utility;
import com.mlib.effects.SoundHandler;
import com.mlib.modhelper.AutoInstance;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.OnDamaged;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class GlassRegenerationEffect extends MobEffect {
	public GlassRegenerationEffect() {
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
	public static class GlassRegeneration {
		private static final SoundHandler GLASS_BREAK = new SoundHandler( SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, SoundHandler.randomized( 0.5f ) );

		public GlassRegeneration() {
			OnDamaged.listen( this::removeEffect )
				.addCondition( Condition.hasEffect( Registries.GLASS_REGENERATION, data->data.target ) )
				.addCondition( OnDamaged.dealtAnyDamage() );
		}

		private void removeEffect( OnDamaged.Data data ) {
			data.target.removeEffect( Registries.GLASS_REGENERATION.get() );
			GLASS_BREAK.play( data.getLevel(), data.target.position() );
		}
	}
}
