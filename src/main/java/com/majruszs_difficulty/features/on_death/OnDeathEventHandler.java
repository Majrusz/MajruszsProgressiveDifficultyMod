package com.majruszs_difficulty.features.on_death;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** Handles all 'OnDeath' events. */
@Mod.EventBusSubscriber
public class OnDeathEventHandler {
	public static final List< IOnDeath > REGISTRY_LIST = new ArrayList<>();

	@SubscribeEvent
	public static void onDeath( LivingDeathEvent event ) {
		DamageSource damageSource = event.getSource();
		@Nullable LivingEntity attacker = damageSource.getEntity() instanceof LivingEntity ? ( LivingEntity )damageSource.getEntity() : null;
		LivingEntity target = event.getEntityLiving();

		for( IOnDeath register : REGISTRY_LIST )
			if( register.shouldBeExecuted( attacker, target, damageSource ) )
				register.onExecute( attacker, target, event );
	}
}
