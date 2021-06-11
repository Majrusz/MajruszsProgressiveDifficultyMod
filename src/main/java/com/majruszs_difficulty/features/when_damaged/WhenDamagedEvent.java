package com.majruszs_difficulty.features.when_damaged;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

/** Handling all 'WhenDamaged' events. */
@Mod.EventBusSubscriber
public class WhenDamagedEvent {
	public static final List< IWhenDamaged > REGISTRY_LIST = new ArrayList<>();

	@SubscribeEvent
	public static void onAttack( LivingHurtEvent event ) {
		DamageSource damageSource = event.getSource();
		LivingEntity attacker = damageSource.getTrueSource() instanceof LivingEntity ? ( LivingEntity )damageSource.getTrueSource() : null;
		LivingEntity target = event.getEntityLiving();

		for( IWhenDamaged register : REGISTRY_LIST )
			if( register.shouldBeExecuted( attacker, target, damageSource ) && event.getAmount() > 0 )
				register.whenDamaged( attacker, target, event );
	}
}
