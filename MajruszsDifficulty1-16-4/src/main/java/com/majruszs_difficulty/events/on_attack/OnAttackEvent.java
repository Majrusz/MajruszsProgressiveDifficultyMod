package com.majruszs_difficulty.events.on_attack;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

/** Handling all 'OnAttack' events. */
@Mod.EventBusSubscriber
public class OnAttackEvent {
	private static final List< OnAttackBase > registryList = new ArrayList<>();

	static {
		registryList.add( new SpiderPoisonOnAttack() );
		registryList.add( new SkyKeeperLevitationOnAttack() );
		registryList.add( new DrownedLightningOnAttack() );
	}

	@SubscribeEvent
	public static void onAttack( LivingHurtEvent event ) {
		DamageSource damageSource = event.getSource();
		if( !( damageSource.getTrueSource() instanceof LivingEntity ) )
			return;

		LivingEntity target = event.getEntityLiving();
		LivingEntity attacker = ( LivingEntity )damageSource.getTrueSource();

		for( OnAttackBase register : registryList )
			if( register.shouldBeExecuted( attacker ) )
				register.onAttack( attacker, target, damageSource );
	}
}
