package com.majruszs_difficulty.effects;

import com.majruszs_difficulty.Instances;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

/** Effect which makes entity immune to Bleeding effect. */
@Mod.EventBusSubscriber
public class BleedingImmunityEffect extends MobEffect {
	public BleedingImmunityEffect() {
		super( MobEffectCategory.BENEFICIAL, 0xff990000 );
	}

	@Override
	public void applyEffectTick( LivingEntity entity, int amplifier ) {}

	@Override
	public void applyInstantenousEffect( @Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity, int amplifier, double health
	) {
		entity.removeEffect( Instances.BLEEDING );
	}

	@Override
	public boolean isDurationEffectTick( int duration, int amplifier ) {
		return false;
	}

	@SubscribeEvent
	public static void onEffectApplied( PotionEvent.PotionApplicableEvent event ) {
		MobEffectInstance effectInstance = event.getPotionEffect();
		LivingEntity entity = event.getEntityLiving();
		if( effectInstance.getEffect() instanceof BleedingEffect && entity.hasEffect( Instances.BLEEDING_IMMUNITY ) )
			event.setResult( Event.Result.DENY );
	}
}
