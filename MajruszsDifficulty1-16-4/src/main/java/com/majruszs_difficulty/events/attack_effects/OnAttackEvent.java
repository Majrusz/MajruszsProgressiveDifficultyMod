package com.majruszs_difficulty.events.attack_effects;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class OnAttackEvent {
	private static final List< OnAttackBase > onAttackList = new ArrayList<>();

	static {
		onAttackList.add( new SpiderPoisonOnAttack(  ) );
	}
	@SubscribeEvent
	public static void onAttack( LivingHurtEvent event ) {
		LivingEntity target = event.getEntityLiving();


		/*if( !shouldExecute( event.getSource(), target ) )
			return;

		ServerWorld world = ( ServerWorld )target.getEntityWorld();

		int poisonDurationInTicks = getPoisonDuration( world.getDifficulty() );

		if( getPoisonChance( target, world ) > MajruszsDifficulty.RANDOM.nextDouble() )
			target.addPotionEffect( new EffectInstance( Effects.POISON, poisonDurationInTicks, 0 ) );*/
	}
}
