package com.majruszs_difficulty.events.when_damaged;

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
	private static final List< WhenDamagedBase > registryList = new ArrayList<>();

	static {
		registryList.add( new SpiderPoisonOnAttack() );
		registryList.add( new SkyKeeperLevitationOnAttack() );
		registryList.add( new DrownedLightningOnAttack() );
		registryList.add( new NauseaAndWeaknessWhenDrowning() );
		registryList.add( new WitherSwordOnAttack() );
		registryList.add( new CactusBleedingOnHurt() );
		registryList.add( new SharpItemBleedingOnAttack() );
		registryList.add( new ArrowBleedingOnHurt() );
		registryList.add( new ThrownTridentBleedingOnHurt() );
		registryList.add( new BiteBleedingOnAttack() );
	}

	@SubscribeEvent
	public static void onAttack( LivingHurtEvent event ) {
		DamageSource damageSource = event.getSource();
		if( !( damageSource.getTrueSource() instanceof LivingEntity ) )
			return;
		LivingEntity attacker = ( LivingEntity )damageSource.getTrueSource();
		LivingEntity target = event.getEntityLiving();

		for( WhenDamagedBase register : registryList )
			if( register.shouldBeExecuted( attacker, target, damageSource ) && event.getAmount() > 0 )
				register.whenDamaged( target );
	}
}
