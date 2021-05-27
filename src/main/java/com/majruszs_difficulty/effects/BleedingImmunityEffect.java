package com.majruszs_difficulty.effects;

import com.majruszs_difficulty.Instances;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

/** Effect making entity immune to Bleeding. */
@Mod.EventBusSubscriber
public class BleedingImmunityEffect extends Effect {
	public BleedingImmunityEffect() {
		super( EffectType.BENEFICIAL, 0xff990000 );
	}

	@Override
	public void performEffect( LivingEntity entity, int amplifier ) {}

	@Override
	public void affectEntity( @Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity, int amplifier, double health ) {
		entity.removePotionEffect( Instances.BLEEDING );
	}

	@Override
	public boolean isReady( int duration, int amplifier ) {
		return false;
	}

	@SubscribeEvent
	public static void onEffectApplied( PotionEvent.PotionApplicableEvent event ) {
		EffectInstance effectInstance = event.getPotionEffect();
		LivingEntity entity = event.getEntityLiving();
		if( effectInstance.getPotion() instanceof BleedingEffect && entity.isPotionActive( Instances.BLEEDING_IMMUNITY ) )
			event.setResult( Event.Result.DENY );
	}
}
