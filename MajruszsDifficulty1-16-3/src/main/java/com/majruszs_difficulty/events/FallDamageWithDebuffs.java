package com.majruszs_difficulty.events;

import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class FallDamageWithDebuffs {
	@SubscribeEvent
	public static void onFall( LivingFallEvent event ) {
		double distance = event.getDistance();

		if( distance < 5.0 )
			return;

		LivingEntity livingEntity = event.getEntityLiving();
		livingEntity.addPotionEffect( new EffectInstance( Effects.SLOWNESS, MajruszsHelper.secondsToTicks( 10.0 + distance * 0.5 ), (int)( distance / 15.0 ) ) );
		livingEntity.addPotionEffect( new EffectInstance( Effects.NAUSEA, MajruszsHelper.secondsToTicks( 6.0 ), (int)( distance / 5.0 ) ) );
	}
}
