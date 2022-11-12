package com.majruszsdifficulty.effects;

import com.majruszsdifficulty.Registries;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnEffectApplicable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

public class BleedingImmunityEffect extends MobEffect {
	public BleedingImmunityEffect() {
		super( MobEffectCategory.BENEFICIAL, 0xff990000 );
	}

	@Override
	public void applyEffectTick( LivingEntity entity, int amplifier ) {}

	@Override
	public void applyInstantenousEffect( @Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity,
		int amplifier, double health
	) {
		entity.removeEffect( Registries.BLEEDING.get() );
	}

	@Override
	public boolean isDurationEffectTick( int duration, int amplifier ) {
		return false;
	}

	@AutoInstance
	public static class BleedingImmunity extends GameModifier {
		public BleedingImmunity() {
			super( Registries.Modifiers.DEFAULT, "BleedingImmunity", "Config for Bleeding Immunity effect." );

			OnEffectApplicable.Context onEffectApplicable = new OnEffectApplicable.Context( this::cancelBleeding );
			onEffectApplicable.addCondition( new Condition.HasEffect( Registries.BLEEDING_IMMUNITY ) )
				.addCondition( data->data.effect.equals( Registries.BLEEDING.get() ) );

			this.addContexts( onEffectApplicable );
		}

		private void cancelBleeding( OnEffectApplicable.Data data ) {
			data.event.setResult( Event.Result.DENY );
		}
	}
}
