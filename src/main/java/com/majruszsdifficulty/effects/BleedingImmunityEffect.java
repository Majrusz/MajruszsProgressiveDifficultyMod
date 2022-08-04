package com.majruszsdifficulty.effects;

import com.majruszsdifficulty.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

/** An effect that makes the entity immune to bleeding. */
@Mod.EventBusSubscriber
public class BleedingImmunityEffect extends MobEffect {
	public BleedingImmunityEffect() {
		super( MobEffectCategory.BENEFICIAL, 0xff990000 );
	}

	@Override
	public void applyEffectTick( LivingEntity entity, int amplifier ) {}

	@Override
	public void applyInstantenousEffect( @Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity, int amplifier, double health ) {
		entity.removeEffect( Registries.BLEEDING.get() );
	}

	@Override
	public boolean isDurationEffectTick( int duration, int amplifier ) {
		return false;
	}

	@SubscribeEvent
	public static void onEffectApplied( PotionEvent.PotionAddedEvent event ) {
		if( event.getPotionEffect().getEffect() instanceof BleedingEffect && event.getEntityLiving().hasEffect( Registries.BLEEDING_IMMUNITY.get() ) )
			event.setResult( Event.Result.DENY );
	}
}
